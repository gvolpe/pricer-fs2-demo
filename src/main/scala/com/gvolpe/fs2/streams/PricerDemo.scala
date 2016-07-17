package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.flow.{OrderGeneratorFlow, PricerFlow}

object PricerDemo extends App {

  import PricerComponents._

  implicit val S = fs2.Strategy.fromFixedDaemonPool(2, "pricer-demo")

  val pricerFlow          = PricerFlow.flow(consumer, logger, storage, pricer, publisher)
  val orderGeneratorFlow  = OrderGeneratorFlow.flow(consumerWriter)

  val program = pricerFlow mergeHaltBoth orderGeneratorFlow

  program.run.unsafeRun()

}
