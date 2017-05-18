package com.gvolpe.fs2.streams.flow

import com.gvolpe.fs2.streams.{PipeT, SinkT}
import com.gvolpe.fs2.streams.model.{Item, Order}
import fs2._

import scala.concurrent.duration._
import scala.util.Random

class OrderGeneratorFlow()(implicit S: Strategy, R: Scheduler) {

  private def defaultOrderGen: PipeT[Int, Order] = { orderIds =>
    val tickInterrupter = time.sleep[Task](11.seconds).map(_ => false) ++ Stream(true)
    val orderTick       = time.awakeEvery[Task](2.seconds).interruptWhen(tickInterrupter)
    (orderIds zip orderTick) flatMap { case (id, _) =>
      val itemId    = Random.nextInt(500).toLong
      val itemPrice = Random.nextInt(10000).toDouble
      val newOrder  = Order(id.toLong, List(Item(itemId, s"laptop-$id", itemPrice)))
      Stream.emit(newOrder)
    }
  }

  def flow(source: SinkT[Order], orderGen: PipeT[Int, Order] = defaultOrderGen): Stream[Task, Unit] = {
    Stream.range(1, 10) through orderGen to source
  }

}
