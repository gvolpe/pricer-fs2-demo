package com.gvolpe.fs2.streams.flow

import cats.effect.IO
import com.gvolpe.fs2.streams.model.{Item, Order}
import fs2._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

class OrderGeneratorFlow()(implicit ec: ExecutionContext, R: Scheduler) {

  private def defaultOrderGen: Pipe[IO, Int, Order] = { orderIds =>
    val tickInterrupter = time.sleep[IO](11.seconds).map(_ => false) ++ Stream(true)
    val orderTick       = time.awakeEvery[IO](2.seconds).interruptWhen(tickInterrupter)
    (orderIds zip orderTick) flatMap { case (id, _) =>
      val itemId    = Random.nextInt(500).toLong
      val itemPrice = Random.nextInt(10000).toDouble
      val newOrder  = Order(id.toLong, List(Item(itemId, s"laptop-$id", itemPrice)))
      Stream.emit(newOrder)
    }
  }

  def flow(source: Sink[IO, Order], orderGen: Pipe[IO, Int, Order] = defaultOrderGen): Stream[IO, Unit] = {
    Stream.range(1, 10).covary[IO] through orderGen to source
  }

}
