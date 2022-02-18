package io.citadel.kernel.eventstore;

import io.citadel.kernel.domain.Attribute;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import java.util.function.Consumer;

@Embeddable
public class EventInfo {
  public String name;

  @Convert(converter = Attribute.AsJson.class)
  public JsonObject data;

  public static EventInfo with(Consumer<EventInfo> consumer) {
    final var event = new EventInfo();
    consumer.accept(event);
    return event;
  }
}