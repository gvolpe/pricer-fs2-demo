package com.gvolpe.fs2.streams.repository

import com.gvolpe.fs2.streams.model.Order
import com.gvolpe.fs2.streams.{SinkT, StreamT}
import fs2.async.mutable.Queue
import fs2.Task

class OrderDb(orderQ: Queue[Task, Order]) {
  def read: StreamT[Order]  = orderQ.dequeue
  def persist: SinkT[Order] = _ to orderQ.enqueue
}
