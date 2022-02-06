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

final class AttributeMap<A, B>
  extends AttributeAbstract<B>
{
  private final AttributeReadableType<A> base;
  private final Function<A, B> transform;

  AttributeMap(
    final Consumer<Throwable> inErrorConsumer,
    final AttributeReadableType<A> inBase,
    final Function<A, B> inTransform)
  {
    super(inErrorConsumer);

    this.base =
      Objects.requireNonNull(inBase, "attr");
    this.transform =
      Objects.requireNonNull(inTransform, "f");

    this.base.subscribe((oldValue, newValue) -> {
      this.publish(
        this.transform.apply(oldValue),
        this.transform.apply(newValue));
    });
  }

  @Override
  public B get()
  {
    return this.transform.apply(this.base.get());
  }

  @Override
  public <C> AttributeReadableType<C> mapR(
    final Function<B, C> f)
  {
    return new AttributeMap<>(this.errors(), this, f);
  }

  @Override
  protected void store(final B b)
  {

  }

  @Override
  public <C> AttributeType<C> map(
    final Function<B, C> f)
  {
    return new AttributeMap<>(this.errors(), this, f);
  }
}
