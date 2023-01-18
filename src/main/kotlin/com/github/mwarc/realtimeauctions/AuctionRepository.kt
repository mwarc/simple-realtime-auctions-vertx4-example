package com.github.mwarc.realtimeauctions

import io.vertx.core.shareddata.SharedData

class AuctionRepository(private val sharedData: SharedData) {

    fun getById(auctionId: String): Auction? {
        val auctionSharedData = sharedData.getLocalMap<String, String>(auctionId)
        return if(auctionSharedData.isNotEmpty()) auctionSharedData.toAuction() else null
    }

    fun save(auction: Auction) {
        val auctionSharedData = sharedData.getLocalMap<String, String>(auction.id)
        auctionSharedData["id"] = auction.id
        auctionSharedData["price"] = auction.price.toString()
    }
}
