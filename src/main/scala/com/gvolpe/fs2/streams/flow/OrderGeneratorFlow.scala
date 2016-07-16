package com.gvolpe.fs2.streams.flow

import com.gvolpe.fs2.streams.{PipeT, SinkT}
import com.gvolpe.fs2.streams.model.{Item, Order}
import fs2._

import scala.concurrent.duration._
import scala.util.Random

object OrderGeneratorFlow {

  implicit val scheduler = fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S         = fs2.Strategy.fromFixedDaemonPool(2, "generator-timer")

  private val defaultOrderGen: PipeT[Int, Order] = { orderIds =>
    val tickInterrupter = time.sleep[Task](11.seconds) ++ Stream(true)
    val orderTick       = time.awakeEvery[Task](2.seconds).interruptWhen(tickInterrupter)
    (orderIds zip orderTick) flatMap { case (id, t) =>
      val itemId    = Random.nextInt(500).toLong
      val itemPrice = Random.nextInt(10000).toDouble
      val newOrder  = Order(id.toLong, List(Item(itemId, s"laptop-$id", itemPrice)))
      Stream.emit(newOrder)
    }
  }

  def flow(source: SinkT[Order], orderGen: PipeT[Int, Order] = defaultOrderGen) = {
    Stream.range(1, 10) through orderGen to source
  }

}
