package io.citadel.domain.forum.aggregate;

import io.citadel.domain.forum.Forum;
import io.citadel.domain.forum.handler.Events;
import io.citadel.kernel.eventstore.data.AggregateInfo;
import io.citadel.kernel.eventstore.data.EventInfo;
import io.citadel.kernel.domain.Eventable;
import io.citadel.kernel.func.ThrowableBiFunction;

import java.util.stream.Stream;

record Root(Model model, long version, Stream<Event> events) implements Aggregate, Eventable<Forum.Event> {
  @Override
  public Aggregate register(final Name name, final Description description) {
    return new Root(model, version, append(events, new Events.Registered(name, description)));
  }

  @Override
  public Aggregate change(final Name name, final Description description) {
    return new Root(model, version, append(events, Forum.events.changed(name, description)));
  }

  @Override
  public Aggregate open() {
    return new Root(model, version, append(events, Forum.events.opened()));
  }

  @Override
  public Aggregate close() {
    return new Root(model, version, append(events, Forum.events.closed()));
  }

  @Override
  public Aggregate archive() {
    return new Root(model, version, append(events, Forum.events.archived()));
  }

  @Override
  public Aggregate reopen() {
    return new Root(model, version, append(events, Forum.events.reopened()));
  }

  @Override
  public boolean is(final State state) {
    return false;
  }

  @Override
  public <T> T commit(final ThrowableBiFunction<? super AggregateInfo, ? super Stream<EventInfo>, ? extends T> transaction) {
    return transaction.apply(aggregate(model.id().toString(), "forum", version), events(events));
  }
}

final class Transaction extends Span<Aggregate> implements Aggregate {
  Transaction(Lifecycle<Aggregate> lifecycle) {
    super(lifecycle, Transaction::new);
  }

  @Override
  public <T> T commit(ThrowableBiFunction<? super AggregateInfo, ? super Stream<EventInfo>, ? extends T> transaction) {
    return service.eventually(aggregate -> aggregate.commit(transaction));
  }
}
