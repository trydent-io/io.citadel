package io.citadel.kernel.media;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

public sealed interface Json {
  enum Namespace implements Json {}

  static JsonObject of(Object... fields) {
    final var json = new JsonObject();
    for (var index = 0; index < fields.length; index += 2) {
      json.put(fields[index].toString(), fields[index + 1]);
    }
    return json;
  }

  static JsonObject parse(String value) {
    return new JsonObject(value);
  }

  static <T> JsonObject from(T any) {
    return JsonObject.mapFrom(any);
  }

  static JsonArray array(JsonObject... jsons) {
    return new JsonArray(List.of(jsons));
  }
}