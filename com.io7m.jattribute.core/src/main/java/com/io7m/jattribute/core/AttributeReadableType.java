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
 * An _attribute_ is an observable value to which one can subscribe and receive
 * updates each time the underlying value changes. Attributes are _functors_ and
 * can therefore be transformed with {@link #mapR(Function)}.
 *
 * Subscribing to an attribute creates a _subscription_, and this subscription
 * must be closed when no longer required. Subscriptions are strong references,
 * and so are capable of preventing attributes from being garbage collected.
 *
 * @param <A> The type of values
 */

public interface AttributeReadableType<A>
{
  /**
   * @return The current value
   */

  A get();

  /**
   * Create a new read-only attribute that is subscribed to this attribute and
   * has its values transformed with {@code f}.
   *
   * @param f   A transform function
   * @param <B> The type of transformed values
   *
   * @return A new attribute
   */

  <B> AttributeReadableType<B> mapR(
    Function<A, B> f);

  /**
   * Subscribe to the attribute. The given receiver function will be evaluated
   * once upon subscription, and then evaluated each time the attribute's value
   * changes. If the receiver function throws an exception, the subscription is
   * automatically closed.
   *
   * @param receiver The receiver function
   *
   * @return A subscription
   */

  AttributeSubscriptionType subscribe(
    AttributeReceiverType<A> receiver);
}
