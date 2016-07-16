package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.flow.{OrderGeneratorFlow, PricerFlow}
import fs2.Stream

object PricerDemo extends App {

  import PricerComponents._

  implicit val S = fs2.Strategy.fromFixedDaemonPool(2, "pricer-demo")

  val program = fs2.concurrent.join(10)(
    Stream(
      PricerFlow.flow(consumer, logger, storage, pricer, publisher),
      OrderGeneratorFlow.flow(consumerWriter)
    )
  )

  program.run.unsafeRun()

}
