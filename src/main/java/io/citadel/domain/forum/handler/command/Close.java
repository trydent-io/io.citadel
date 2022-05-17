package io.citadel.domain.forum.handler.command;

import io.citadel.domain.forum.Forum;
import io.citadel.domain.forum.handler.Commands;
import io.citadel.kernel.domain.Actor;

public record Close() implements Actor.Behaviour<Forum.Aggregate, Commands.Close> {

  @Override
  public void be(Forum.Aggregate aggregate, Commands.Close close, String by) {

  }
}
