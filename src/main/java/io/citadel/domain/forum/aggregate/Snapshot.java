package io.citadel.domain.forum.aggregate;

import io.citadel.domain.forum.Forum;
import io.citadel.domain.forum.handler.Events;
import io.citadel.kernel.domain.Domain;
import io.citadel.kernel.func.Maybe;
import io.citadel.kernel.func.ThrowablePredicate;
import io.vertx.core.json.JsonObject;

import static io.citadel.domain.forum.handler.Events.Names.valueOf;

public sealed interface Snapshot extends Forum<Snapshot>, Domain.Snapshot<Aggregate, Forum.ID> {
  static Snapshot hydration() {
    return new Hydration(new Lifecycle());
  }

  record Hydration(Lifecycle lifecycle, Forum.Model model, long version) implements Snapshot {
    Hydration(Lifecycle lifecycle) {this(lifecycle, null, -1);}

    @Override
    public Domain.Snapshot<Aggregate, Forum.ID> apply(Forum.ID aggregateId, long aggregateVersion, String eventName, JsonObject eventData) {
      final var snapshot = model == null ? new Hydration(lifecycle, new Model(aggregateId), version) : this;
      return
        (
          switch (valueOf(eventName)) {
            case Opened -> snapshot.open();
            case Closed -> snapshot.close();
            case Registered -> snapshot.register(eventData.mapTo(Events.Registered.class).details());
            case Reopened -> snapshot.reopen();
            case Replaced -> snapshot.replace(eventData.mapTo(Events.Replaced.class).details());
            case Archived -> snapshot.archive();
          }
        ).otherwise("Can't hydrate aggregate for Forum with ID %s".formatted(aggregateId.toString()), IllegalStateException::new);
    }

    private ThrowablePredicate<Lifecycle> byModel(final Forum.ID id) {
      return it -> model != null && model.id().equals(id);
    }

    @Override
    public Aggregate aggregate() {
      return new Root(model, version, new Transaction(lifecycle));
    }

    @Override
    public Maybe<Snapshot> register(final Details details) {
      return lifecycle.register(details).map(it -> new Hydration(it, new Forum.Model(model.id(), details), version));
    }

    @Override
    public Maybe<Snapshot> replace(final Details details) {
      return lifecycle.replace(details).map(it -> new Hydration(it, new Forum.Model(model.id(), details), version));
    }

    @Override
    public Maybe<Snapshot> open() {
      return lifecycle.open().map(it -> new Hydration(it, model, version));
    }

    @Override
    public Maybe<Snapshot> close() {
      return lifecycle.close().map(it -> new Hydration(it, model, version));
    }

    @Override
    public Maybe<Snapshot> archive() {
      return lifecycle.archive().map(it -> new Hydration(it, model, version));
    }

    @Override
    public Maybe<Snapshot> reopen() {
      return lifecycle.reopen().map(it -> new Hydration(it, model, version));
    }
  }

}
