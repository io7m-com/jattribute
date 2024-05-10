/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jattribute.core.internal;

import com.io7m.jattribute.core.AttributeReadableType;
import com.io7m.jattribute.core.AttributeType;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A function attribute that evaluates a function each time a value is
 * requested.
 *
 * @param <A> The type of underlying values
 */

public final class AttributeFunction<A> extends AttributeAbstract<A>
{
  private final Supplier<A> value;

  /**
   * Construct a new attribute.
   *
   * @param inErrorConsumer The error consumer
   * @param supplier        The supplier function
   */

  public AttributeFunction(
    final Consumer<Throwable> inErrorConsumer,
    final Supplier<A> supplier)
  {
    super(inErrorConsumer);

    this.value =
      Objects.requireNonNull(supplier, "initial");
  }

  @Override
  public A get()
  {
    return this.value.get();
  }

  @Override
  public <B> AttributeReadableType<B> mapR(
    final Function<A, B> f)
  {
    return new AttributeMap<>(this.errors(), this, f);
  }

  @Override
  protected void store(final A a)
  {

  }

  @Override
  public <B> AttributeType<B> map(
    final Function<A, B> f)
  {
    return new AttributeMap<>(this.errors(), this, f);
  }
}
