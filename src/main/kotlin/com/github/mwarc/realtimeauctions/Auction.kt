package com.github.mwarc.realtimeauctions

import io.vertx.core.shareddata.LocalMap
import java.math.BigDecimal

data class Auction(
  val id: String,
  val price: BigDecimal
) {

  constructor(id: String) : this(id, BigDecimal.ZERO)
}

fun LocalMap<String, String>.toAuction() = Auction(
  id = this["id"]!!,
  price = BigDecimal(this["price"])
)
