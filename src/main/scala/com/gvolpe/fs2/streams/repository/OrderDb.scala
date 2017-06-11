package com.gvolpe.fs2.streams.repository

import cats.effect.IO
import com.gvolpe.fs2.streams.model.Order
import fs2.async.mutable.Queue
import fs2.{Sink, Stream}

class OrderDb(orderQ: Queue[IO, Order]) {
  def read: Stream[IO, Order]  = orderQ.dequeue
  def persist: Sink[IO, Order] = _ to orderQ.enqueue
}
