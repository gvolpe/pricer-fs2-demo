package com.gvolpe.fs2

import cats.effect.IO
import com.gvolpe.fs2.streams.model.Order
import fs2.Pipe

package object streams {

  def log(action: String): Pipe[IO, Order, Order] = _.evalMap (order => showOrder(action, order) map (_ => order))

  def showOrder(action: String, order: Order): IO[Unit] = IO {
    println(s"$action order ${order.id} with items ${order.items.map(_.toString).mkString(" | ")}")
  }

}
