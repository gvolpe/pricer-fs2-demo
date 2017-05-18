package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.async.mutable.Topic
import fs2.Task

class OrderKafkaBroker(topic: Topic[Task, Order]) extends Broker {
  override def consume: StreamT[Order] = topic.subscribe(100)
  override def produce: SinkT[Order]   = _ to topic.publish
}
