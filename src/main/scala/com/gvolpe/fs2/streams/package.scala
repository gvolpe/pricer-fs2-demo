package com.gvolpe.fs2

import com.gvolpe.fs2.streams.model.Order
import fs2.{Pipe, Sink, Stream, Task}

package object streams {

  type StreamT[A]   = Stream[Task, A]
  type PipeT[A, B]  = Pipe[Task, A, B]
  type SinkT[A]     = Sink[Task, A]

  def log(action: String): PipeT[Order, Order] = _.evalMap (order => showOrder(action, order) map (_ => order))

  def showOrder(action: String, order: Order) = Task.now {
    println(s"$action order ${order.id} with items ${order.items.map(_.toString).mkString(" | ")}")
  }

}
