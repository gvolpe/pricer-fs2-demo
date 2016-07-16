package com.gvolpe.fs2.streams.broker
import com.gvolpe.fs2.streams.StreamT
import com.gvolpe.fs2.streams.model.Order
import fs2.{Task, async}

object OrderKafkaBroker extends Broker {

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, threadName = "kafka-broker")

  val ordersQ = async.boundedQueue[Task, Order](100)

  override def consume: StreamT[Order] = ordersQ.unsafeRun().dequeue

  override def produce(order: Order): Task[Unit] = ordersQ.unsafeRun().enqueue1(order)
}
