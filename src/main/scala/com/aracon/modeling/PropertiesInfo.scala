package com.aracon.modeling

import cats._
import cats.implicits._

object PropertiesInfo {

  // Modeling with Abstract Algebra is all about finding the properties that our domain holds.
  // Properties = specific values irrelevant, we care about properties when viewing inputs and outputs to our domain
  // at a more abstract level.
  // Using properties can save a lot of effort, both in code not written as well as in shorter and more reliable tests.

  // Some important properties:

  // Closure — Given two elements of the same type, we combine them to produce another element of the same type.
  def closureInt(a: Int, b: Int): Int = a + b
  def closureString(a: String, b: String): String = a + b
  def closure[A : Monoid](a: A, b: A):A = Monoid[A].combine(a, b)
  // In the example above, note 'Closure' can be over an abstract type, which makes it easier to find generic properties.
  // In our example, we could test the function follows Monoid combine laws to verify it works.

  // Associativity — We can place parenthesis anywhere we’d like in a sequence of combinations
  // Translating to code, we could say that both methods are equivalent:
  def associative[A : Monoid](a: A, b: A, c: A): A =   Monoid[A].combine(Monoid[A].combine(a, b), c)
  def associative2[A : Monoid](a: A, b: A, c: A): A =   Monoid[A].combine(a, Monoid[A].combine(b, c))
  // If we know a type A we have supports associative operations, we can do things like create a Semigroup for it, which
  // in turn allows us to use it in some contents directly (fold, etc) with no extra effort.
  // We can use cats-laws to verify is a function is associative, which would allow the same (use that function as implementation
  // of a Semigroup, same benefits)

  // Identity — There is a special value we can apply that has no affect on the outcome
  // Identity value depends on the operation, and can also be tested using cats-laws. Knowing an identity value (Monoid.empty)
  // means, among others, that we can use default values in operations where we don't have enough input, or in tests to
  // avoid influencing the output.

  // Similarly, in cats we have Id[A] == A, as you can see below
  type B = String
  val a: B = ""
  val idA : Id[B] = a
  a === idA
  // Id[A] can be used as a replacement value in some case where we need an unconditional wrapper (like F[_]) but we don't have one available.


  // Commutativity — The order of the elements doesn’t matter
  // Commutativity helps with parallelism: if our operations (functions) can be proven to be commutative (cats-laws)
  // then we can, for example, use them with Applicative to run operations in parallel, aggregating results at the end, knowing
  // completion order won't impact the final result. Can allow us to use Applicative instead of monadic for.


  // Idempotence — Receiving the same message multiple times has no affect after the first time
  // Standard example in web development are Get requests, given no other changes in the backend.
  // If we can prove idempotence on our operations, we can avoid issues in distributed messages which usually have an 'at-least-once' guarantee
  // which in turn means we must be able to handle multiple repetitions of the message or we can have unexpected behaviour.
  // In this scenario, ensuring idempotence would mean we don't need ot write a lot of code + tests to verify behaviour.


  // Examples:
  // Ordering: order of 2 elements is closed, associative, id (say always elements are the same, no order is modified), and idempotent. But not commutative.
  // As a results, we know Ordering is a Monoid (Idempotent Monoid), we can use that abstraction to both test it and implement behaviour.

  //

}
