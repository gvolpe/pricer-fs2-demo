package com.gvolpe.fs2.streams.repository

import com.gvolpe.fs2.streams.model.Order
import com.gvolpe.fs2.streams.{SinkT, StreamT}
import fs2.{Task, async}

object OrderDb {

  implicit val S     = fs2.Strategy.fromFixedDaemonPool(8, "order-db")

  private val orderQ = async.boundedQueue[Task, Order](100).unsafeRun()

  def read: StreamT[Order]  = orderQ.dequeue
  def persist: SinkT[Order] = _.evalMap(orderQ.enqueue1)

  // TODO: Make it work using streams instead of queue.unsafeRun()
//  val orderQ     = Stream.eval(async.boundedQueue[Task, Order](100))
//  def persist: SinkT[Order] = input => orderQ.flatMap(q => input to q.enqueue)
//  def read: StreamT[Order]  = orderQ.flatMap(_.dequeue)
}
