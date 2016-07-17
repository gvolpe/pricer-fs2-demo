package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams._
import com.gvolpe.fs2.streams.model.Order
import fs2.{Task, async}

object OrderKafkaBroker extends Broker {

  implicit val S     = fs2.Strategy.fromFixedDaemonPool(8, "kafka-broker")

  private val orderT = async.topic[Task, Order](Order.Empty).unsafeRun()

  override def consume: StreamT[Order] = orderT.subscribe(100)

  override def produce: SinkT[Order]   = _.evalMap(orderT.publish1)

  // TODO: Make it work using streams instead of topic.unsafeRun()
//  private val orderT = Stream.eval(async.topic[Task, Order](Order.Empty))
//  override def consume: StreamT[Order] = orderT.flatMap(_.subscribe(100))
//  override def produce: SinkT[Order] = input => orderT.flatMap(t => input through log("Kafka") to t.publish)
}
