package com.gvolpe.fs2.streams

import cats.effect.IO
import fs2.{Sink, Stream}

object model {

  type ItemId  = Long
  type OrderId = Long

  case class OrderStorage(read: Stream[IO, Order], write: Sink[IO, Order])

  case class Item(id: ItemId, name: String, price: Double) {
    override def toString = s"$name : $price"
  }
  case class Order(id: OrderId, items: List[Item])

  object Order {
    val Empty = Order(0L, List.empty[Item])
  }

}
