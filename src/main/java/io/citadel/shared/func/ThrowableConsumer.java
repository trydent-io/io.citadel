package io.citadel.shared.func;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableConsumer<A> extends Consumer<A> {
  @Override
  default void accept(A a) {
    try {
      tryAccept(a);
    } catch (Throwable e) {
      throw new FunctionalException("Can't accept consumer", e);
    }
  }

  void tryAccept(A a) throws Throwable;

  default <T> T accept(T $this, A a) {
    accept(a);
    return $this;
  }
}
