package io.citadel.domain.forum.repository;

import io.citadel.domain.forum.Forum;
import io.citadel.domain.forum.Forums;
import io.citadel.domain.forum.aggregate.Aggregate;
import io.citadel.eventstore.data.AggregateInfo;
import io.citadel.eventstore.data.EventInfo;
import io.citadel.kernel.domain.Domain;
import io.vertx.core.Future;

import java.util.stream.Stream;

public record Repository(Domain.Aggregates<Aggregate, Forum.ID> aggregates) implements Forums {
  @Override
  public Future<Aggregate> load(final Forum.ID id) {
    return aggregates.load(id);
  }

  @Override
  public Future<Void> save(AggregateInfo aggregate, Stream<EventInfo> events) {
    return aggregates.save(aggregate, events);
  }
}
