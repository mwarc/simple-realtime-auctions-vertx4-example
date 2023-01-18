package com.github.mwarc.realtimeauctions

import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext

import java.math.BigDecimal

class AuctionHandler(
    private val repository: AuctionRepository,
    private val validator: AuctionValidator
) {

    fun handleGetAuction(context: RoutingContext) {
        val auctionId = context.request().getParam("id")
        val auction: Auction? = repository.getById(auctionId)
        if (auction != null) {
            context.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(200)
                .end(Json.encodePrettily(auction))
        } else {
            context.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(404)
                .end()
        }
    }

    fun handleChangeAuctionPrice(context: RoutingContext) {
        val auctionId = context.request().getParam("id")
        val auctionRequest = Auction(
            auctionId,
            BigDecimal(context.body().asJsonObject().getString("price"))
        )
        if (validator.validate(auctionRequest)) {
            repository.save(auctionRequest)
            context.vertx().eventBus().publish("auction.$auctionId", context.body().asString())
            context.response()
                .setStatusCode(200)
                .end()
        } else {
            context.response()
                .setStatusCode(422)
                .end()
        }
    }

    fun initAuctionInSharedData(context: RoutingContext) {
        val auctionId = context.request().getParam("id")
        val auction: Auction? = repository.getById(auctionId)
        if (auction == null) {
            repository.save(Auction(auctionId))
        }
        context.next()
    }
}