package com.gvolpe.fs2.streams.broker

import cats.effect.IO
import com.gvolpe.fs2.streams.model.Order
import fs2.{Sink, Stream}

trait Broker {
  def consume: Stream[IO, Order]
  def produce: Sink[IO, Order]
}
