package com.github.mwarc.realtimeauctions

class AuctionValidator(private val repository: AuctionRepository) {

    fun validate(auction: Auction): Boolean {
        val (_, price) = repository.getById(auction.id) ?: throw AuctionNotFoundException(auction.id)
        return price.compareTo(auction.price) == -1
    }
}