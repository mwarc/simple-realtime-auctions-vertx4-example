package com.github.mwarc.realtimeauctions

class AuctionNotFoundException(auctionId: String) : RuntimeException("Auction not found: $auctionId")
