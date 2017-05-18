package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.async.mutable.Queue
import fs2.Task

class OrderRabbitMqBroker(orderQ: Queue[Task, Order]) extends Broker {
  override def consume: StreamT[Order] = orderQ.dequeue
  override def produce: SinkT[Order]   = _ through log("Publishing") to orderQ.enqueue
}
