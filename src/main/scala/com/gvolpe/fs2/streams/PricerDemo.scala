package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.flow.{OrderGeneratorFlow, PricerFlow}
import fs2.Stream

object PricerDemo extends App {

  import PricerComponents._

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, threadName = "pricer-demo")

  val pricerFlow = PricerFlow.flow(consumer, logger, storage, pricer, publisher)(S)
  val generator  = OrderGeneratorFlow.flow(consumerWriter)

  val program = fs2.concurrent.join(10)(
    Stream(
      pricerFlow,
      generator
    )
  )

  program.run.unsafeRun()

}
