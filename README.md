jattribute
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jattribute/com.io7m.jattribute.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jattribute%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/com.io7m.jattribute/com.io7m.jattribute.svg?style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/jattribute/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m/jattribute.svg?style=flat-square)](https://codecov.io/gh/io7m/jattribute)

![jattribute](./src/site/resources/jattribute.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m/jattribute/workflows/main.linux.temurin.current.yml?branch=develop)](https://github.com/io7m/jattribute/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m/jattribute/workflows/main.linux.temurin.lts.yml?branch=develop)](https://github.com/io7m/jattribute/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m/jattribute/workflows/main.windows.temurin.current.yml?branch=develop)](https://github.com/io7m/jattribute/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m/jattribute/workflows/main.windows.temurin.lts.yml?branch=develop)](https://github.com/io7m/jattribute/actions?query=workflow%3Amain.windows.temurin.lts)|


### Features

* Type-safe, mutable, observable values.
* Tiny codebase.
* 100% automated test suite coverage.
* [OSGi](https://www.osgi.org) ready.
* [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System) ready.
* ISC license.

### Usage

Create an instance of the `Attributes` class to create new attributes. The class
takes a function as an argument that will receive exceptions raised by
subscribers.

```
var attributes = Attributes.create(e -> LOG.error("exception raised: ", e));
```

#### Creating Attributes

Use the `attributes` instance to create new attribute values. The values held in
an attribute _should_ be of an immutable class. This is not strictly required,
but the purpose of attributes is ostensibly to communicate state updates to
consumers, and mutating a value held in an attribute will _not_ cause
subscribers to be notified that anything has changed.

The following code creates an integer-typed attribute initialized with a value
of `23`:

```
var ival = attributes.create(23);
```

Attributes implement the `AttributeType` interface, which allows for both
reading and writing values. `AttributeType` is a subtype of `AttributeReadableType`,
which is a read-only interface. Code that should not be allowed to write to
an attribute can be passed the attribute as a value of type
`AttributeReadableType`.

Attributes are thread-safe and can be written to and read from any number
of threads.

#### Subscribing To Attributes

Consumers can subscribe to state updates. Subscribing to an attribute creates
a _subscription_ that must be closed when no longer needed. Subscriptions create
strong references, and so can prevent attributes from being garbage collected.
It's important to be aware of this in applications that are frequently creating
and discarding attributes.

```
var sub = ival.subscribe((oldValue, newValue) -> {
  ...
});
```

Subscriptions implement `AutoCloseable` and can therefore be used with
`try-with-resources`:

```
try (var sub = ival.subscribe((oldValue, newValue) -> {
  ...
})) {
  ...
}
```

If a subscriber raises an exception on receipt of a state update, the
subscriber's subscription is automatically closed. The exception raised will be
delivered to the function passed to the `Attributes` class above. The rationale
for this is that the client that modified the attribute should not receive an
exception if one of the subscribers failed to handle the state update properly,
and none of the other subscribers should be subjected to the errors of one
failing subscriber. The failing subscriber failed to handle the exception, and
we don't want to just discard the exception silently.

Subscriber functions are called on the same thread that updated the attribute.

#### Updating Attributes

Use the `set` method to update the value held in an attribute.

```
ival.set(25);
```

All subscribers to `ival` will be notified immediately that the value has 
changed from `23` to `25`. 

#### Transforming Attributes

Attributes are functors, and so the `map` method (`mapR` for read-only 
attributes) can be used to produce a new attribute `K` that will transform 
values from an existing attribute `M` each time `M` is updated.

```
var dval = ival.map(i -> (double) i);
```

Each time `ival` is updated, the subscribers of `dval` will see the transformed
value in state updates. Subscribers of `ival` are _not_ automatically
subscribed to `dval`; conceptually, it is an entirely new and distinct
attribute.

