package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.broker.{OrderKafkaBroker, OrderRabbitMqBroker}
import com.gvolpe.fs2.streams.model.{Order, OrderStorage}
import com.gvolpe.fs2.streams.repository.OrderDb
import com.gvolpe.fs2.streams.service.PricerService

object PricerComponents {

  import com.gvolpe.fs2.streams.utils.FS2Utils._

  val storage                       = OrderStorage(OrderDb.read, OrderDb.persist)

  val consumerWriter: SinkT[Order]  = OrderKafkaBroker.produce
  val consumer: StreamT[Order]      = OrderKafkaBroker.consume

  val pricer: PipeT[Order, Order]   = liftPipe[Order, Order](PricerService.updatePrices)

  val publisher: SinkT[Order]       = OrderRabbitMqBroker.produce

  val logger: SinkT[Order]          = liftSink[Order](showOrder("Consuming", _))

}
