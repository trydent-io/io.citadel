package io.citadel.domain.forum.aggregate;

import io.citadel.domain.forum.Forum;

import java.util.stream.Stream;

public record Sourcing(Model model) implements Forum.Snapshot {
  @Override
  public Forum register(final Name name, Description description) {
    return new Sourcing(new Model(model.id(), new Details(name, description)));
  }

  @Override
  public Forum open() {
    return this;
  }

  @Override
  public Forum close() {
    return this;
  }

  @Override
  public Forum archive() {
    return this;
  }

  @Override
  public Forum reopen() {
    return this;
  }

  @Override
  public Forum edit(final Name name, Description description) {
    return new Sourcing(new Model(model.id(), new Details(name, description)));
  }

  @Override
  public Forum.Transaction transaction(final long version) {
    return new Providing(model, version, Stream.empty());
  }
}
