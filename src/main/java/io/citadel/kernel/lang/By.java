package io.citadel.kernel.lang;

import io.citadel.kernel.func.TryBiFunction;
import io.citadel.kernel.func.ThrowableFunction;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;

public sealed interface By {
  enum Namespace implements By {}

  static <T, R> Collector<T, ?, R> reducing(R identity, TryBiFunction<? super R, ? super T, ? extends R> function) {
    return collectingAndThen(Collectors.reducing(ThrowableFunction.<R>identity(), t -> r -> function.apply(r, t), ThrowableFunction::then), finalizer -> finalizer.apply(identity));
  }
}
