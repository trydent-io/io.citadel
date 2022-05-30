package io.citadel.kernel.domain.model;

import io.citadel.kernel.eventstore.Meta;
import io.citadel.kernel.domain.Domain;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.stream.Stream;

public final class Service extends AbstractVerticle implements Domain.Verticle {
  private final Domain.Migration domain;
  private final Migration migration;
  private final EventStore eventStore;

  Service(final Migration migration, final EventStore eventStore) {
    this.migration = migration;
    this.eventStore = eventStore;
  }

  @Override
  public void start(final Promise<Void> start) {
    migration.apply()
      .map(this::operations)
      .onSuccess(start::complete)
      .onFailure(start::fail);
  }

  private Void operations(Void unused) {
    vertx.eventBus().<JsonObject>localConsumer(SEEK,
      message -> seek(message.body())
        .onSuccess(message::reply)
        .onFailure(it -> message.fail(500, it.getMessage()))
    );

    vertx.eventBus().<JsonObject>localConsumer(FEED,
      message -> persist(
        message.body().getJsonObject("aggregate"),
        message.body().getJsonObject("event"),
        message.body().getJsonObject("persisted")
      )
        .onSuccess(message::reply)
        .onFailure(it -> message.fail(500, it.getMessage()))
    );
    return null;
  }

  private Future<Meta> persist(final JsonObject aggregate, JsonObject event, JsonObject persisted) {
    return null;
  }

  private Future<Meta> seek(final JsonObject aggregate) {
    return seek(aggregate.mapTo(Meta.Aggregate.class));
  }

  @Override
  public Future<Meta> seek(Meta.Aggregate aggregate) {
    return eventStore.findPrototype(aggregate);
  }

  @Override
  public Future<Meta> feed(final Meta.Aggregate aggregate, final Stream<Meta.Event> events, final String by) {
    return null;
  }

/*  @Override
  public Future<Feed> feed(Feed.Aggregate aggregate, Feed.Event event, Feed.Persisted persisted) {
    return eventStore.feed(aggregate, event, persisted);
  }*/
}
