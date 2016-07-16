package com.gvolpe.fs2.streams.flow

import com.gvolpe.fs2.streams.{PipeT, SinkT}
import com.gvolpe.fs2.streams.model.{Item, Order}
import fs2.{Stream, Task, pipe}

import scala.util.Random

object OrderGeneratorFlow {

  private val defaultOrderGen: PipeT[Int, Order] = pipe.lift[Task, Int, Order] { orderId =>
    Thread.sleep(2000)
    val itemId = Random.nextInt(500).toLong
    val itemPrice = Random.nextInt(10000).toDouble
    Order(orderId.toLong, List(Item(itemId, s"laptop-$orderId", itemPrice)))
  }

  def flow(source: SinkT[Order], orderGen: PipeT[Int, Order] = defaultOrderGen) = {
    Stream.range(1, 10) through orderGen to source
  }

}
