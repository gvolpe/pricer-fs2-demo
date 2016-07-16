package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.{Task, async}

object OrderRabbitMqBroker extends Broker {

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, threadName = "rabbit-mq-broker")

  val orderQ = async.boundedQueue[Task, Order](100)

  override def consume: StreamT[Order] = orderQ.unsafeRun().dequeue

  override def produce(order: Order): Task[Unit] = {
    showOrder("Publishing", order) flatMap (_ => orderQ.unsafeRun().enqueue1(order))
  }
}
