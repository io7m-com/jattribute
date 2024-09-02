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

import com.io7m.jattribute.core.AttributeReceiverType;
import com.io7m.jattribute.core.AttributeSubscriptionType;
import com.io7m.jattribute.core.AttributeType;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * An abstract base attribute.
 *
 * @param <A> The type of values.
 */

abstract class AttributeAbstract<A>
  implements AttributeType<A>
{
  private final CopyOnWriteArrayList<Subscription<A>> subscribers;
  private final Consumer<Throwable> errorConsumer;

  protected AttributeAbstract(
    final Consumer<Throwable> inErrorConsumer)
  {
    this.errorConsumer =
      Objects.requireNonNull(inErrorConsumer, "errorConsumer");
    this.subscribers =
      new CopyOnWriteArrayList<>();
  }

  protected final Consumer<Throwable> errors()
  {
    return this.errorConsumer;
  }

  @Override
  public final AttributeSubscriptionType subscribe(
    final AttributeReceiverType<A> receiver)
  {
    Objects.requireNonNull(receiver, "receiver");

    final var subscription = new Subscription<A>(this, receiver);
    this.subscribers.add(subscription);
    final var current = this.get();

    try {
      receiver.receive(current, current);
    } catch (final Throwable e) {
      this.consumeException(subscription, e);
    }
    return subscription;
  }

  protected abstract void store(A a);

  @Override
  public final A set(
    final A newValue)
  {
    Objects.requireNonNull(newValue, "y");

    final var oldValue = this.get();
    this.store(newValue);
    this.publish(oldValue, newValue);
    return newValue;
  }

  protected final void publish(
    final A oldValue,
    final A newValue)
  {
    for (final var subscriber : this.subscribers) {
      try {
        if (!subscriber.isClosed()) {
          subscriber.receiver.receive(oldValue, newValue);
        }
      } catch (final Throwable e) {
        this.consumeException(subscriber, e);
      }
    }
  }

  private void consumeException(
    final Subscription<A> subscriber,
    final Throwable e)
  {
    try (subscriber) {
      this.errorConsumer.accept(e);
    } catch (final Throwable ignored) {
      // Nothing we can do.
    }
  }

  private static final class Subscription<A>
    implements AttributeSubscriptionType
  {
    private final AttributeAbstract<A> attribute;
    private final AttributeReceiverType<A> receiver;
    private final AtomicBoolean closed;

    private Subscription(
      final AttributeAbstract<A> inAttribute,
      final AttributeReceiverType<A> inReceiver)
    {
      this.attribute =
        Objects.requireNonNull(inAttribute, "attribute");
      this.receiver =
        Objects.requireNonNull(inReceiver, "receiver");
      this.closed =
        new AtomicBoolean(false);
    }

    @Override
    public void close()
      throws RuntimeException
    {
      if (this.closed.compareAndSet(false, true)) {
        this.attribute.subscribers.remove(this);
      }
    }

    @Override
    public boolean isClosed()
    {
      return this.closed.get();
    }
  }
}
