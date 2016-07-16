package com.gvolpe.fs2.streams.flow

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model._
import fs2.Stream

object PricerFlow {

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, "pricer-flow")

  def flow(consumer: StreamT[Order],
           logger: SinkT[Order],
           storage: OrderStorage,
           pricer: PipeT[Order, Order],
           publisher: SinkT[Order])(implicit S: fs2.Strategy) = {
    fs2.concurrent.join(10)(
      Stream(
        consumer      observe logger to storage.write,
        storage.read  through pricer to publisher
      )
    )
  }

}
