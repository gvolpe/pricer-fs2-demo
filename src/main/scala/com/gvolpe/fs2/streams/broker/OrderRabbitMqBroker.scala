package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.async.mutable.Queue
import fs2.{Stream, Task, async}

object OrderRabbitMqBroker extends Broker {

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, "rabbit-mq-broker")

  val qqq = async.boundedQueue[Task, Order](100)
  val orderQ = Stream.eval(qqq)

  override def consume: StreamT[Order] = orderQ.flatMap(q => q.dequeue)

  def produce2(input: StreamT[Order])  = orderQ.flatMap(q => input to q.enqueue)

  override def produce(order: Order): Task[Unit] = orderQ.evalMap { q =>
    showOrder("Publishing", order) flatMap (_ => q.enqueue1(order))
  }.run
//  override def produce(order: Order): StreamT[Unit] = orderQ.evalMap { q =>
//    showOrder("Publishing", order) flatMap (_ => q.enqueue1(order))
//  }
}
