package com.gvolpe.fs2.streams.broker

import cats.effect.IO
import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.async.mutable.Queue
import fs2.{Sink, Stream}

class OrderRabbitMqBroker(orderQ: Queue[IO, Order]) extends Broker {
  override def consume: Stream[IO, Order] = orderQ.dequeue
  override def produce: Sink[IO, Order]   = _ through log("Publishing") to orderQ.enqueue
}
