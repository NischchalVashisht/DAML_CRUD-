/*
 * THIS FILE WAS AUTOGENERATED BY THE DIGITAL ASSET DAML SCALA CODE GENERATOR
 * DO NOT EDIT BY HAND!
 */
import _root_.com.digitalasset.ledger.client.{binding=>$u0020lfdomainapi}
import _root_.com.digitalasset.ledger.api.v1.{value=>$u0020rpcvalue}
package com.knoldus.model {
  package Main {
    final case class IouProposal_Accept() extends ` lfdomainapi`.ValueRef

    object IouProposal_Accept extends ` lfdomainapi`.ValueRefCompanion with _root_.scala.Function0[_root_.com.knoldus.model.Main.IouProposal_Accept] {
      import _root_.scala.language.higherKinds;
      trait view[` C`[_]] extends ` lfdomainapi`.encoding.RecordView[` C`, view] { $u0020view =>
        final override def hoist[` D`[_]](` f` : _root_.scalaz.~>[` C`, ` D`]): view[` D`] = {
          final class $anon extends _root_.scala.AnyRef with view[` D`];
          new $anon()
        }
      };
      implicit val `IouProposal_Accept Value`: ` lfdomainapi`.Value[_root_.com.knoldus.model.Main.IouProposal_Accept] = {
        final class $anon extends this.`Value ValueRef`[_root_.com.knoldus.model.Main.IouProposal_Accept] {
          override def write(value: _root_.com.knoldus.model.Main.IouProposal_Accept): ` rpcvalue`.Value.Sum = ` record`();
          override def read(argValue: ` rpcvalue`.Value.Sum): _root_.scala.Option[_root_.com.knoldus.model.Main.IouProposal_Accept] = argValue.record.flatMap(((x$2) => _root_.scala.Some(IouProposal_Accept())))
        };
        new $anon()
      };
      override protected val ` dataTypeId` = ` mkDataTypeId`(`Package IDs`.Main, "Main", "IouProposal_Accept");
      implicit def `IouProposal_Accept LfEncodable`: ` lfdomainapi`.encoding.LfEncodable[_root_.com.knoldus.model.Main.IouProposal_Accept] = {
        final class $anon extends ` lfdomainapi`.encoding.LfEncodable[_root_.com.knoldus.model.Main.IouProposal_Accept] {
          override def encoding(lte: ` lfdomainapi`.encoding.LfTypeEncoding): lte.Out[_root_.com.knoldus.model.Main.IouProposal_Accept] = {
            object `view ` extends view[lte.Field];
            lte.emptyRecord(` dataTypeId`, (() => _root_.com.knoldus.model.Main.IouProposal_Accept()))
          }
        };
        new $anon()
      }
    }
  }
}
