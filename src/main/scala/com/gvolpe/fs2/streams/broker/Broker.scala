package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams.StreamT
import com.gvolpe.fs2.streams.model.Order
import fs2.Task

trait Broker {
  def consume: StreamT[Order]
  def produce(order: Order): Task[Unit]
}
