package com.gvolpe.fs2.streams

import com.gvolpe.fs2.streams.broker.{OrderKafkaBroker, OrderRabbitMqBroker}
import com.gvolpe.fs2.streams.model.{Order, OrderStorage}
import com.gvolpe.fs2.streams.repository.OrderDb
import com.gvolpe.fs2.streams.service.PricerService
import fs2.{Sink, Stream, Task}

object PricerComponents {

  import com.gvolpe.fs2.streams.utils.FS2Utils._

  val storageWriter: SinkT[Order]   = liftSink[Order](OrderDb.persist)
  val storage                       = OrderStorage(OrderDb.orderQ.dequeue, storageWriter)

//  val consumerWriter: SinkT[Order]  = _.flatMap(OrderKafkaBroker.produce)
  val consumerWriter: SinkT[Order]  = liftSink[Order](OrderKafkaBroker.produce)
  val consumer: StreamT[Order]      = OrderKafkaBroker.consume

  val pricer: PipeT[Order, Order]   = liftPipe[Order, Order](PricerService.updatePrices)

  val publisher: SinkT[Order]       = liftSink[Order](OrderRabbitMqBroker.produce)
//  val publisher: SinkT[Order]       = OrderRabbitMqBroker.produce2

  val logger: SinkT[Order]          = liftSink[Order](showOrder("Consuming", _))

}
