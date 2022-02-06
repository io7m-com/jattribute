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

package com.io7m.jattribute.core;

import java.util.function.Function;

/**
 * @param <A> The type of underlying values
 *
 * @see AttributeReadableType
 */

public interface AttributeType<A> extends AttributeReadableType<A>
{
  /**
   * Set the value of this attribute. Any subscribers of this observable
   * are notified of the change in value.
   *
   * @param y The new value
   *
   * @return The old value
   */

  A set(A y);

  /**
   * Create a new attribute that is subscribed to this attribute and has its
   * values transformed with {@code f}.
   *
   * @param f   A transform function
   * @param <B> The type of transformed values
   *
   * @return A new attribute
   */

  <B> AttributeType<B> map(
    Function<A, B> f);
}
