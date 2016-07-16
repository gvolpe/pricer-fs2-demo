package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.broker.{OrderKafkaBroker, OrderRabbitMqBroker}
import com.gvolpe.fs2.streams.model.{Order, OrderStorage}
import com.gvolpe.fs2.streams.repository.OrderDb
import com.gvolpe.fs2.streams.service.PricerService
import fs2.{Task, pipe}

object PricerComponents {

  // Another way of writing the storage writer
  val customWriter: SinkT[Order] = { st => st flatMap { order =>
    fs2.Stream.emit(OrderDb.persist(order).unsafeRun())
  }}

  val storageWriter               = pipe.lift[Task, Order, Unit](order => OrderDb.persist(order).unsafeRun())
  val storage                     = OrderStorage(OrderDb.orderQ.dequeue, storageWriter)

  val consumerWriter              = pipe.lift[Task, Order, Unit](order => OrderKafkaBroker.produce(order).unsafeRun())
  val consumer: StreamT[Order]    = OrderKafkaBroker.consume

  val pricer: PipeT[Order, Order] = pipe.lift[Task, Order, Order](order => PricerService.updatePrices(order))

  val publisher: SinkT[Order]     = pipe.lift[Task, Order, Unit](order => OrderRabbitMqBroker.produce(order).unsafeRun())

  val logger: SinkT[Order]        = pipe.lift[Task, Order, Unit](order => showOrder("Consuming", order).unsafeRun())

}
