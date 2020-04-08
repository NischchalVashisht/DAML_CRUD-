package com.knoldus

import java.time.Instant

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.digitalasset.api.util.TimeProvider
import com.digitalasset.grpc.adapter.AkkaExecutionSequencerPool
import com.digitalasset.ledger.api.refinements.ApiTypes.{ApplicationId, WorkflowId}
import com.digitalasset.ledger.api.v1.ledger_offset.LedgerOffset
import com.digitalasset.ledger.client.LedgerClient
import com.digitalasset.ledger.client.binding.{Contract, Primitive}
import com.digitalasset.ledger.client.configuration.{CommandClientConfiguration, LedgerClientConfiguration, LedgerIdRequirement}
import com.knoldus.ClientUtil.workflowIdFromParty
import com.knoldus.DecodeUtil.{decodeAllCreated, decodeArchived, decodeCreated}
import com.knoldus.FutureUtil.toFuture
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

// <doc-ref:imports>
import com.digitalasset.ledger.client.binding.{Primitive => P}
import com.knoldus.model.{Main=>M}

// </doc-ref:imports>


object ProductMain extends App with StrictLogging {
  if (args.length != 2) {
    logger.error("Usage: LEDGER_HOST LEDGER_PORT")
    System.exit(-1)
  }

  private val ledgerHost = args(0)
  private val ledgerPort = args(1).toInt

  private val manufacturer = P.Party("emami")
  private val distributor = P.Party("bigbazar")
  private val buyer = P.Party("consumer")

  private val asys = ActorSystem()
  private val amat = Materializer(asys)
  private val aesf = new AkkaExecutionSequencerPool("clientPool")(asys)

  private def shutdown(): Unit = {
    logger.info("Shutting down...")
    Await.result(asys.terminate(), 10.seconds)
    ()
  }

  private implicit val ec: ExecutionContext = asys.dispatcher

  private val applicationId = ApplicationId("Product-chain Example")

  private val timeProvider = TimeProvider.Constant(Instant.EPOCH)

  private val clientConfig = LedgerClientConfiguration(
    applicationId = ApplicationId.unwrap(applicationId),
    ledgerIdRequirement = LedgerIdRequirement("", enabled = false),
    commandClient = CommandClientConfiguration.default,
    sslContext = None,
    token = None
  )

  private val clientF: Future[LedgerClient] =
    LedgerClient.singleHost(ledgerHost, ledgerPort, clientConfig)(ec, aesf)

  private val clientUtilF: Future[ClientUtil] =
    clientF.map(client => new ClientUtil(client, applicationId, 30.seconds, timeProvider))

  private val offset0F: Future[LedgerOffset] = clientUtilF.flatMap(_.ledgerEnd)

  private val issuerWorkflowId: WorkflowId = workflowIdFromParty(manufacturer)

  private val newOwnerWorkflowId: WorkflowId = workflowIdFromParty(distributor)

  val iou = M.Product(manufacturer,manufacturer,manufacturer,"MyProduct",manufacturer)

  val issuerFlow: Future[Unit] = for {
    clientUtil <- clientUtilF
    offset0 <- offset0F
    _ = logger.info(s"Client API initialization completed, Ledger ID: ${clientUtil.toString}")
    createCmd = iou.create
    _ <- clientUtil.submitCommand(manufacturer, issuerWorkflowId, createCmd)
    _ = logger.info(s"$manufacturer created IOU: $iou")
    _ = logger.info(s"$manufacturer sent create command: $createCmd")

    tx0 <- clientUtil.nextTransaction(manufacturer, offset0)(amat)
    _ = logger.info(s"$manufacturer received transaction: $tx0")
    manufContract <- toFuture(decodeCreated[M.Product](tx0))
    _ = logger.info(s"$manufacturer received contract: $manufContract")

    offset1 <- clientUtil.ledgerEnd

    exerciseCmd = manufContract.contractId.exerciseTransfer(actor = manufacturer, newOwner = distributor,newWholeSaler = distributor,newConsumer = distributor)

    _ <- clientUtil.submitCommand(manufacturer, issuerWorkflowId, exerciseCmd)
    _ = logger.info(s"$manufacturer sent exercise command: $exerciseCmd")
    _ = logger.info(s"$manufacturer transferred IOU: $manufContract to: $distributor")



    tx1 <- clientUtil.nextTransaction(manufacturer, offset1)(amat)
    _ = logger.info(s"$manufacturer received final transaction: $tx1")

    //exerciseCmd.exerciseAcceptOwnership(distributor,)

    archivedProductContract <- toFuture(decodeArchived[M.Product](tx1)): Future[P.ContractId[M.Product]]
    _ = logger.info(
        s"$manufacturer received Archive Event for the original IOU contract ID: $archivedProductContract")
    _ <- Future(assert(manufContract.contractId == archivedProductContract))
    productTransferContract <- toFuture(decodeAllCreated[M.TransferOwnership](tx1).headOption)
    _ = logger.info(s"$manufacturer received confirmation for the IOU Transfer: $productTransferContract")


    offset2 <- clientUtil.ledgerEnd

    exerciseSecondCmd = manufContract.contractId.exerciseTransfer(actor = distributor, newOwner = buyer,newWholeSaler = distributor,newConsumer = buyer)
    _ <- clientUtil.submitCommand(d, issuerWorkflowId, exerciseCmd)


  } yield ()

  val returnCodeF: Future[Int] = issuerFlow.transform {
    case Success(_) =>
      logger.info("IOU flow completed.")
      Success(0)
    case Failure(e) =>
      logger.error("IOU flow completed with an error", e)
      Success(1)
  }

  val returnCode: Int = Await.result(returnCodeF, 10.seconds)
  shutdown()
  System.exit(returnCode)

  }
