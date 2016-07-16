package com.gvolpe.fs2.streams.service

import com.gvolpe.fs2.streams.model.Order

object PricerService {

  private val MarginProfit = 1.05

  def updatePrices(order: Order): Order = {
    val updatedItems = order.items.map(i => i.copy(price = i.price * MarginProfit))
    order.copy(items = updatedItems)
  }

}
