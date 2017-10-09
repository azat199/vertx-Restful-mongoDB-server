package io.vertx;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;


public class MongoClientVerticle extends AbstractVerticle {

    private MongoClient client;

    @Override
    public void start(Future<Void> fut) {

        Router router = Router.router(vertx);
        router.route("/").handler(StaticHandler.create("assets"));


        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(

                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );

        router.get("/api/users").handler(this::getAll);

        router.route("/api/users*").handler(BodyHandler.create());
        router.post("/api/users").handler(this::addOne);

        router.delete("/api/users/:id").handler(this::deleteOne);

    }

    private void getAll(RoutingContext routingContext) {

        client = MongoClient.createShared(vertx, config(), "users");

        client.find("users", new JsonObject(), res -> {
          if (res.succeeded()) {
              routingContext.response()
                  .putHeader("content-type", "application/json; charset=utf-8")
                  .end(res.result().toString());
              System.out.println(res.result().toString());
          } else {
            res.cause().printStackTrace();
          }
        });
    }

    private void addOne(RoutingContext routingContext) {

        client = MongoClient.createShared(vertx, config(), "users");

        User user = Json.decodeValue(routingContext.getBodyAsString(), User.class);

        client.insert("users", user.toJson(), res -> {
            if (res.succeeded()) {
                System.out.println("Inserted User" + user.getId());
            } else {
                res.cause().printStackTrace();
            }
        });

    }

    private void deleteOne(RoutingContext routingContext) {

        client = MongoClient.createShared(vertx, config(), "users");

        String id = routingContext.request().getParam("id");

        if(id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            client.removeOne("users", new JsonObject().put("_id", id),
                    res -> routingContext.response().setStatusCode(204).end());
        }
    }
}


