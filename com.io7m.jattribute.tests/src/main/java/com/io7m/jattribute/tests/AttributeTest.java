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

package com.io7m.jattribute.tests;

import com.io7m.jattribute.core.Attributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class AttributeTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(AttributeTest.class);

  private ArrayList<String> events;
  private Attributes attributes;
  private int errors;

  @BeforeEach
  public void setup()
  {
    this.events = new ArrayList<String>();
    this.errors = 0;

    this.attributes = Attributes.create(throwable -> {
      LOG.error("error: ", throwable);
      ++this.errors;
    });
  }

  /**
   * Subscribing to an attribute works.
   */

  @Test
  public void testAttributeBasic()
  {
    final var attr0 =
      this.attributes.withValue(23);

    final var sub0 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s0 " + oldValue + " " + newValue);
      });

    final var sub1 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s1 " + oldValue + " " + newValue);
      });

    attr0.set(25);
    attr0.set(26);
    sub1.close();
    attr0.set(27);

    assertEquals("attr0 s0 23 23", this.events.remove(0));
    assertEquals("attr0 s1 23 23", this.events.remove(0));
    assertEquals("attr0 s0 23 25", this.events.remove(0));
    assertEquals("attr0 s1 23 25", this.events.remove(0));
    assertEquals("attr0 s0 25 26", this.events.remove(0));
    assertEquals("attr0 s1 25 26", this.events.remove(0));
    assertEquals("attr0 s0 26 27", this.events.remove(0));
    assertEquals(0, this.events.size());
    assertEquals(0, this.errors);
  }

  /**
   * Subscribing to a function attribute works.
   */

  @Test
  public void testAttributeFunction()
  {
    final var attr0 =
      this.attributes.withValue(23);

    final var sub0 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s0 " + oldValue + " " + newValue);
      });

    final var sub1 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s1 " + oldValue + " " + newValue);
      });

    attr0.set(25);
    attr0.set(26);
    sub1.close();
    attr0.set(27);

    assertEquals("attr0 s0 23 23", this.events.remove(0));
    assertEquals("attr0 s1 23 23", this.events.remove(0));
    assertEquals("attr0 s0 23 25", this.events.remove(0));
    assertEquals("attr0 s1 23 25", this.events.remove(0));
    assertEquals("attr0 s0 25 26", this.events.remove(0));
    assertEquals("attr0 s1 25 26", this.events.remove(0));
    assertEquals("attr0 s0 26 27", this.events.remove(0));
    assertEquals(0, this.events.size());
    assertEquals(0, this.errors);
  }

  /**
   * Subscribing to a mapped attribute works.
   */

  @Test
  public void testAttributeMapped()
  {
    final var attr0 =
      this.attributes.withValue(23);
    final var attr1 =
      attr0.map(i -> Double.valueOf(i.doubleValue()));

    final var sub0 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s0 " + oldValue + " " + newValue);
      });
    final var sub1 =
      attr1.subscribe((oldValue, newValue) -> {
        this.events.add("attr1 s1 " + oldValue + " " + newValue);
      });

    attr0.set(25);
    sub0.close();
    attr0.set(26);

    assertEquals("attr0 s0 23 23", this.events.remove(0));
    assertEquals("attr1 s1 23.0 23.0", this.events.remove(0));
    assertEquals("attr1 s1 23.0 25.0", this.events.remove(0));
    assertEquals("attr0 s0 23 25", this.events.remove(0));
    assertEquals("attr1 s1 25.0 26.0", this.events.remove(0));
    assertEquals(0, this.events.size());
    assertEquals(0, this.errors);
  }

  /**
   * Subscribing to a mapped attribute works.
   */

  @Test
  public void testAttributeMappedR()
  {
    final var attr0 =
      this.attributes.withValue(23);
    final var attr1 =
      attr0.mapR(i -> Double.valueOf(i.doubleValue()));

    final var sub0 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s0 " + oldValue + " " + newValue);
      });
    final var sub1 =
      attr1.subscribe((oldValue, newValue) -> {
        this.events.add("attr1 s1 " + oldValue + " " + newValue);
      });

    attr0.set(25);
    sub0.close();
    attr0.set(26);

    assertEquals("attr0 s0 23 23", this.events.remove(0));
    assertEquals("attr1 s1 23.0 23.0", this.events.remove(0));
    assertEquals("attr1 s1 23.0 25.0", this.events.remove(0));
    assertEquals("attr0 s0 23 25", this.events.remove(0));
    assertEquals("attr1 s1 25.0 26.0", this.events.remove(0));
    assertEquals(0, this.events.size());
    assertEquals(0, this.errors);
  }

  /**
   * Heavily mapping an attribute works.
   */

  @Test
  public void testAttributeMapHeavy()
  {
    final var attr0 =
      this.attributes.withValue(23);
    final var attr1 =
      attr0.map(i -> i.doubleValue())
        .map(i -> i * i)
        .map(i -> 2 * i)
        .mapR(i -> i.intValue());

    final var sub0 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s0 " + oldValue + " " + newValue);
      });
    final var sub1 =
      attr1.subscribe((oldValue, newValue) -> {
        this.events.add("attr1 s1 " + oldValue + " " + newValue);
      });

    attr0.set(25);

    assertEquals("attr0 s0 23 23", this.events.remove(0));
    assertEquals("attr1 s1 1058 1058", this.events.remove(0));
    assertEquals("attr1 s1 1058 1250", this.events.remove(0));
    assertEquals("attr0 s0 23 25", this.events.remove(0));
    assertEquals(0, this.events.size());
    assertEquals(0, this.errors);
  }

  /**
   * A crashing consumer isn't a problem.
   */

  @Test
  public void testConsumerCrashes()
  {
    final var attr0 =
      this.attributes.withValue(23);

    final var sub0 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s0 " + oldValue + " " + newValue);
      });
    final var sub1 =
      attr0.subscribe((oldValue, newValue) -> {
        throw new OutOfMemoryError("FAMOUS LAST WORDS!");
      });

    attr0.set(25);

    assertTrue(sub1.isClosed());
    assertEquals("attr0 s0 23 23", this.events.remove(0));
    assertEquals("attr0 s0 23 25", this.events.remove(0));
    assertEquals(0, this.events.size());
    assertEquals(1, this.errors);
  }

  /**
   * A (late) crashing consumer isn't a problem.
   */

  @Test
  public void testConsumerCrashesLate()
  {
    final var attr0 =
      this.attributes.withValue(23);

    final var sub0 =
      attr0.subscribe((oldValue, newValue) -> {
        this.events.add("attr0 s0 " + oldValue + " " + newValue);
      });

    final var count = new AtomicInteger(0);
    final var sub1 =
      attr0.subscribe((oldValue, newValue) -> {
        final var now = count.incrementAndGet();
        if (now >= 2) {
          throw new OutOfMemoryError("FAMOUS LAST WORDS!");
        } else {
          this.events.add("attr0 s1 " + oldValue + " " + newValue);
        }
      });

    assertFalse(sub1.isClosed());
    attr0.set(25);
    assertTrue(sub1.isClosed());

    assertEquals("attr0 s0 23 23", this.events.remove(0));
    assertEquals("attr0 s1 23 23", this.events.remove(0));
    assertEquals("attr0 s0 23 25", this.events.remove(0));
    assertEquals(0, this.events.size());
    assertEquals(1, this.errors);
  }
}
