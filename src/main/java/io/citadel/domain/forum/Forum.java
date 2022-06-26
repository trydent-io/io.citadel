package io.citadel.domain.forum;

import io.citadel.domain.forum.Forum.Event.Archived;
import io.citadel.domain.forum.Forum.Event.Closed;
import io.citadel.domain.forum.Forum.Event.Opened;
import io.citadel.domain.forum.Forum.Event.Registered;
import io.citadel.domain.forum.Forum.Event.Reopened;
import io.citadel.domain.forum.Forum.Event.Replaced;
import io.citadel.domain.forum.Forum.Forums;
import io.citadel.domain.member.Member;
import io.citadel.kernel.eventstore.EventStore;
import io.citadel.kernel.eventstore.meta.Aggregate;
import io.citadel.kernel.eventstore.meta.Version;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.citadel.kernel.eventstore.meta.Aggregate.*;
import static io.citadel.kernel.eventstore.meta.Aggregate.name;

public sealed interface Forum {
  String FORUM = "forum";
  static ID id(final String value) {
    return new ID(UUID.fromString(value));
  }
  static Entity entity(JsonObject json) {
    return json.mapTo(Entity.class);
  }

  static State state(String value) {
    return Forum.State.valueOf(value);
  }
  enum State implements io.citadel.kernel.domain.State<State, Event> {
    Registered, Open, Closed, Archived;
    @Override
    public State transit(final Event event) {
      return switch (event) {
        case Registered it && this.is(Registered) -> this;
        case Replaced it && this.is(Registered, Open) -> this;
        case Opened it && this.is(Registered) -> Open;
        case Closed it && this.is(Open) -> Closed;
        case Reopened it && this.is(Closed) -> Registered;
        case Archived it && this.is(Closed) -> Archived;
        default -> throw new IllegalStateException("Can't transit to next state, state %s and event %s don't satisfy invariant".formatted(this, event));
      };
    }
  }
  sealed interface Command {
    record Register(Name name, Description description, LocalDateTime at) implements Command {
    }
    record Open(LocalDateTime at) implements Command {
    }
    record Replace(Name name, Description description) implements Command {
    }
    record Close(LocalDateTime at) implements Command {
    }
    record Reopen(LocalDateTime at, Member.ID memberID) implements Command {
    }
    record Archive() implements Command {
    }
  }
  sealed interface Event {
    enum Names {Opened, Closed, Registered, Reopened, Replaced, Archived}
    record Registered(Details details) implements Event {
    }
    record Replaced(Details details) implements Event {
    }
    record Opened() implements Event {
    }
    record Closed() implements Event {
    }
    record Reopened() implements Event {
    }
    record Archived() implements Event {
    }
  }
  record ID(UUID value) {
  } // ID
  record Name(String value) {
  } // part of Details
  record Description(String value) {
  } // part of Details
  record Details(Name name, Description description) {
  } // ValueObject for Details

  record Entity(Details details, Member.ID registeredBy) {
  }

  sealed interface Forums {
    Future<Forum> lookup(Forum.ID id);
  }
}

final class Metadata implements Forums {
  private final EventStore eventStore;

  Metadata(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @Override
  public Future<Forum> lookup(Forum.ID id) {
    return eventStore.aggregate(id, Forum.FORUM)
      .map(aggregate -> switch (aggregate) {
        case Identity it -> Forum.aggregate(id, it.version());
        case Entity it -> Forum.aggregate(
          it.id().as(Forum::id),
          it.version(),
          it.data().as(Forum::entity),
          it.state().as(Forum::state)
        );
      });
  }
}


