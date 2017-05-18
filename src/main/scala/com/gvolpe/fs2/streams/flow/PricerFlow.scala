package com.gvolpe.fs2.streams.flow

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model._
import fs2.{Strategy, Stream, Task}

class PricerFlow()(implicit S: Strategy) {

  def flow(consumer: StreamT[Order],
           logger: SinkT[Order],
           storage: OrderStorage,
           pricer: PipeT[Order, Order],
           publisher: SinkT[Order])(implicit S: fs2.Strategy): Stream[Task, Unit] = {
    fs2.concurrent.join(2)(
      Stream(
        consumer      observe logger to storage.write,
        storage.read  through pricer to publisher
      )
    )
  }

}
