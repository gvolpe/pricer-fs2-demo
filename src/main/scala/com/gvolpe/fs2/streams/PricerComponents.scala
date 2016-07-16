package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.broker.{OrderKafkaBroker, OrderRabbitMqBroker}
import com.gvolpe.fs2.streams.model.{Order, OrderStorage}
import com.gvolpe.fs2.streams.repository.OrderDb
import com.gvolpe.fs2.streams.service.PricerService
import fs2.{Task, pipe}

object PricerComponents {

  val storageWriter = pipe.lift[Task, Order, Unit](order => OrderDb.persist(order))
  val storage = OrderStorage(OrderDb.orderQ.unsafeRun().dequeue, storageWriter)

  val consumerWriter = pipe.lift[Task, Order, Unit](order => OrderKafkaBroker.produce(order))
  val consumer: StreamT[Order] = OrderKafkaBroker.consume

  val pricer: PipeT[Order, Order] = pipe.lift[Task, Order, Order](order => PricerService.updatePrices(order))

  val publisher: SinkT[Order] = pipe.lift[Task, Order, Unit](order => OrderRabbitMqBroker.produce(order))

  val logger: SinkT[Order]  = pipe.lift[Task, Order, Unit](order => showOrder("Consuming", order))

}
