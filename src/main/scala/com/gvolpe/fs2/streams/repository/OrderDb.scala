package com.gvolpe.fs2.streams.repository

import com.gvolpe.fs2.streams.model.Order
import fs2.{Task, async}

object OrderDb {

  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, "order-db")

  val orderQ = async.boundedQueue[Task, Order](100).unsafeRun()

  def persist(order: Order): Task[Unit] = orderQ.enqueue1(order)
}
