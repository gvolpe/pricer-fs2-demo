package com.gvolpe.fs2.streams.service

import com.gvolpe.fs2.streams.model.Order
import fs2.Task

object PricerService {

  private val MarginProfit = 1.05

  def updatePrices(order: Order): Task[Order] = Task.now {
    val updatedItems = order.items.map(i => i.copy(price = i.price * MarginProfit))
    order.copy(items = updatedItems)
  }

}
