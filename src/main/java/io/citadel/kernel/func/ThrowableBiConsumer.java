package io.citadel.kernel.func;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowableBiConsumer<A, B> extends BiConsumer<A, B> {
  @Override
  default void accept(A a, B b) {
    try {
      tryAccept(a, b);
    } catch (Throwable e) {
      throw new LambdaException("Can't accept consumer", e);
    }
  }

  void tryAccept(A a, B b) throws Throwable;
}
