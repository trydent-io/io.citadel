package io.citadel.eventstore.data;

import io.citadel.kernel.media.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Feed extends Iterable<Feed.Entry> {
  record Aggregate(String id, String name, long version) {
    public Aggregate(String id, String name) { this(id, name, -1); }
  }
  record Event(String name, JsonObject data) {}
  record Persisted(LocalDateTime at, String by) {}
  record Entry(UUID id, Aggregate aggregate, Event event, Persisted persisted) {
    public Entry(Aggregate aggregate, Event event) {
      this(null, aggregate, event, null);
    }
  }

  static Feed fromJson(JsonObject json) {
    return from(json.getJsonArray("entries"));
  }

  static Feed from(JsonArray array) {
    return new Type.Entries(array.stream()
      .map(Json::fromAny)
      .map(it -> it.mapTo(Entry.class))
      .toArray(Entry[]::new));
  }

  static Feed fromRows(RowSet<Row> rows) {
    return from(Json.array(rows));
  }

  default Stream<Feed.Entry> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  default JsonArray asJsonArray() {
    return Json.array(this);
  }

  enum Type {;
    private record Entries(Feed.Entry... entries) implements Feed {
      @Override
      public Iterator<Entry> iterator() {
        return List.of(entries).iterator();
      }
    }
  }
}