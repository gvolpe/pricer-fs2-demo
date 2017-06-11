package com.gvolpe.fs2.streams

import cats.effect.IO
import com.gvolpe.fs2.streams.broker.{OrderKafkaBroker, OrderRabbitMqBroker}
import com.gvolpe.fs2.streams.flow.{OrderGeneratorFlow, PricerFlow}
import com.gvolpe.fs2.streams.model.{Order, OrderStorage}
import com.gvolpe.fs2.streams.repository.OrderDb
import com.gvolpe.fs2.streams.service.PricerService
import com.gvolpe.fs2.streams.utils.FS2Utils._
import fs2.{Pipe, Sink, Stream, async}

object PricerDemo extends App {

  implicit val R = fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S = scala.concurrent.ExecutionContext.Implicits.global

  val pricer: Pipe[IO, Order, Order]   = liftPipe[Order, Order](PricerService.updatePrices)

  val logger: Sink[IO, Order]          = liftSink[Order](showOrder("Consuming", _))

  val pricerProgram = for {
    kafkaTopic    <- Stream.eval(async.topic[IO, Order](Order.Empty))
    rabbitQueue   <- Stream.eval(async.boundedQueue[IO, Order](100))
    dbQueue       <- Stream.eval(async.boundedQueue[IO, Order](100))
    kafkaBroker   = new OrderKafkaBroker(kafkaTopic)
    rabbitBroker  = new OrderRabbitMqBroker(rabbitQueue)
    db            = new OrderDb(dbQueue)
    pricerFlow    = new PricerFlow().flow(kafkaBroker.consume, logger, OrderStorage(db.read, db.persist), pricer, rabbitBroker.produce)
    orderGenFlow  = new OrderGeneratorFlow().flow(kafkaBroker.produce)
    program       <- pricerFlow mergeHaltBoth orderGenFlow
  } yield program

  pricerProgram.run.unsafeRunSync()

}
