package com.gvolpe.fs2.streams.repository

import com.gvolpe.fs2.streams.model.Order
import fs2.{Task, async}

object OrderDb {

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, threadName = "order-db")

  val orderQ = async.boundedQueue[Task, Order](100)

  def persist(order: Order): Task[Unit] = orderQ.unsafeRun().enqueue1(order)
}
