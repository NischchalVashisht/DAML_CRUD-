# DAML_CRUD-

## Following example shows Tri-party contract between
 1: Manufacturer
 2: Distributor / wholesaler
 3: Consumer

There are two possible cases where Manufacturer provides his service to Wholesaler.
By providing a JobWork facility to Wholesaler where Wholesaler come with request of   producing a particular product with definite quantity.
By producing their own product and making a proposal to Wholesaler.


 With production of any product a ownership tag is attached with it so in case of Job work    product is produced by manufacturer but ownership of product is held to Client. While in cases where the manufacturer produces their own product  ownership lies to the manufacturer.

## Compile
Create dar file using
```bash
daml build
```
Generate scala code of templates using
```bash
daml codegen scala
```
Finally start the sandbox with the dar file created at first step
```bash
daml sandbox ./.daml/dist/quickstart-0.0.1.dar
```
Open another terminal and run the application using
```bash
sbt “application/runMain com.knoldus.Operation localhost 6865”
```
## Scenarios covered

Case where Manufacture goes to wholesaler and wholesaler accepts the product then ownership transfer from manufacturer to wholesaler.
Case where Manufacturer produces product on the request of Client in such case Client is Owner from beginning.

## Scenario execution
Run test scenarios using 
```bash
daml damlc -- test --files Main.daml
```
