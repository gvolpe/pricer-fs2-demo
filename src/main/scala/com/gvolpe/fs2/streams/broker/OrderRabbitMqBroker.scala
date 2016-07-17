package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.{Stream, Task, async}

object OrderRabbitMqBroker extends Broker {

  implicit val S     = fs2.Strategy.fromFixedDaemonPool(8, "rabbit-mq-broker")

  private val orderQ = Stream.eval(async.boundedQueue[Task, Order](100))

  override def consume: StreamT[Order] = orderQ.flatMap(_.dequeue)

  override def produce: SinkT[Order]   = input => orderQ.flatMap(q => input through log("Publishing") to q.enqueue)
}
