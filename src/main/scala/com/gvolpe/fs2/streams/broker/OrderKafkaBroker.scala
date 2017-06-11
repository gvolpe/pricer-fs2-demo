package com.gvolpe.fs2.streams.broker

import cats.effect.IO
import com.gvolpe.fs2.streams.model.Order
import fs2.async.mutable.Topic
import fs2.{Sink, Stream}

class OrderKafkaBroker(topic: Topic[IO, Order]) extends Broker {
  override def consume: Stream[IO, Order] = topic.subscribe(100)
  override def produce: Sink[IO, Order]   = _ to topic.publish
}
