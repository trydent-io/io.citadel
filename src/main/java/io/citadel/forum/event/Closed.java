package io.citadel.forum.event;

import io.citadel.forum.Forum;
import io.citadel.forum.command.Close;
import io.citadel.member.MemberID;

import java.time.LocalDateTime;

public record Closed(LocalDateTime at, MemberID by) implements Forum.Event {
  @Override
  public Forum.Command asCommand() {
    return new Close(at, by);
  }
}
