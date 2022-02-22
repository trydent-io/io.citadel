package io.citadel.forum.command;

import io.citadel.forum.Forum;
import io.citadel.forum.event.Opened;
import io.citadel.member.MemberID;

import java.time.LocalDateTime;

public record Open(LocalDateTime at, MemberID by) implements Forum.Command {
  @Override
  public Forum.Event asEvent() {
    return new Opened(at, by);
  }
}
