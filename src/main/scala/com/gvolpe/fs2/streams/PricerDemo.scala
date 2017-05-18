package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.broker.{OrderKafkaBroker, OrderRabbitMqBroker}
import com.gvolpe.fs2.streams.flow.{OrderGeneratorFlow, PricerFlow}
import com.gvolpe.fs2.streams.model.{Order, OrderStorage}
import com.gvolpe.fs2.streams.repository.OrderDb
import com.gvolpe.fs2.streams.service.PricerService
import com.gvolpe.fs2.streams.utils.FS2Utils._
import fs2.{Stream, Task, async}

object PricerDemo extends App {

  implicit val R = fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S = fs2.Strategy.fromFixedDaemonPool(8, "pricer-demo")

  val pricer: PipeT[Order, Order]   = liftPipe[Order, Order](PricerService.updatePrices)

  val logger: SinkT[Order]          = liftSink[Order](showOrder("Consuming", _))

  val pricerProgram = for {
    kafkaTopic    <- Stream.eval(async.topic[Task, Order](Order.Empty))
    rabbitQueue   <- Stream.eval(async.boundedQueue[Task, Order](100))
    dbQueue       <- Stream.eval(async.boundedQueue[Task, Order](100))
    kafkaBroker   = new OrderKafkaBroker(kafkaTopic)
    rabbitBroker  = new OrderRabbitMqBroker(rabbitQueue)
    db            = new OrderDb(dbQueue)
    pricerFlow    = new PricerFlow().flow(kafkaBroker.consume, logger, OrderStorage(db.read, db.persist), pricer, rabbitBroker.produce)
    orderGenFlow  = new OrderGeneratorFlow().flow(kafkaBroker.produce)
    program       <- pricerFlow mergeHaltBoth orderGenFlow
  } yield program

  pricerProgram.run.unsafeRun()

}
