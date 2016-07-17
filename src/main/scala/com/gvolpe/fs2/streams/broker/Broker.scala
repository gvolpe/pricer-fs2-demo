package com.gvolpe.fs2.streams.broker

import com.gvolpe.fs2.streams.{SinkT, StreamT}
import com.gvolpe.fs2.streams.model.Order

trait Broker {
  def consume: StreamT[Order]
  def produce: SinkT[Order]
}
