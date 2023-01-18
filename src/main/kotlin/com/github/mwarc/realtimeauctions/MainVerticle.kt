package com.github.mwarc.realtimeauctions

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.bridge.BridgeEventType
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ErrorHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler


class MainVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

    override fun start(startPromise: Promise<Void>) {
        val router = Router.router(vertx)

        router.route("/eventbus/*").subRouter(eventBusRouter())
        router.route("/api/*").subRouter(auctionApiRouter())
        router.route().failureHandler(errorHandler())
        router.route().handler(staticHandler())

        vertx.createHttpServer().requestHandler(router).listen(8080)
    }

    private fun eventBusRouter(): Router {
        val options: SockJSBridgeOptions = SockJSBridgeOptions()
            .addOutboundPermitted(PermittedOptions().setAddressRegex("auction\\.[0-9]+"))
        return SockJSHandler.create(vertx).bridge(options) { event ->
            if (event.type() === BridgeEventType.SOCKET_CREATED) {
                logger.info("A socket was created")
            }
            event.complete(true)
        }
    }

    private fun auctionApiRouter(): Router {
        val repository = AuctionRepository(vertx.sharedData())
        val validator = AuctionValidator(repository)
        val handler = AuctionHandler(repository, validator)

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.route().consumes("application/json")
        router.route().produces("application/json")

        router.route("/auctions/:id").handler { context -> handler.initAuctionInSharedData(context) }
        router.get("/auctions/:id").handler { context -> handler.handleGetAuction(context) }
        router.patch("/auctions/:id").handler { context -> handler.handleChangeAuctionPrice(context) }

        return router
    }

    private fun errorHandler(): ErrorHandler {
        return ErrorHandler.create(vertx, true)
    }

    private fun staticHandler(): StaticHandler {
        return StaticHandler.create()
            .setCachingEnabled(false)
    }
}
