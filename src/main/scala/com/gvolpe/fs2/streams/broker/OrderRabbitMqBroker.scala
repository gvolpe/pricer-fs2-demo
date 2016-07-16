package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.{Task, async}

object OrderRabbitMqBroker extends Broker {

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, "rabbit-mq-broker")

  private val orderQ = async.boundedQueue[Task, Order](100).unsafeRun()

  override def consume: StreamT[Order] = orderQ.dequeue

  override def produce(order: Order): Task[Unit] = {
    showOrder("Publishing", order) flatMap (_ => orderQ.enqueue1(order))
  }
}
