package com.gvolpe.fs2.streams.flow

import cats.effect.IO
import com.gvolpe.fs2.streams.model._
import fs2.{Pipe, Sink, Stream}

import scala.concurrent.ExecutionContext

class PricerFlow() {

  def flow(consumer: Stream[IO, Order],
           logger: Sink[IO, Order],
           storage: OrderStorage,
           pricer: Pipe[IO, Order, Order],
           publisher: Sink[IO, Order])
          (implicit ec: ExecutionContext): Stream[IO, Unit] = {
    Stream(
      consumer      observe logger to storage.write,
      storage.read  through pricer to publisher
    ).join(2)
  }

}
