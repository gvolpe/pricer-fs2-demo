package com.gvolpe.fs2.streams

object model {

  type ItemId  = Long
  type OrderId = Long

  case class OrderStorage(read: StreamT[Order], write: SinkT[Order])

  case class Item(id: ItemId, name: String, price: Double) {
    override def toString = s"$name : $price"
  }
  case class Order(id: OrderId, items: List[Item])

}
