/*
 * Copyright 2018 Pere Villega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aracon.catsinfo

import cats.arrow.Arrow
import cats.data.Kleisli
import cats.implicits._

object Arrows {

  // Arrows is a type class for modeling composable relationships between two types.
  // It generalises functions: if `arr` is an instance of Arrow, a value of type `b arr c` can be thought of as a computation which takes values of
  // type b as input, and produces values of type c as output.
  // In the (->) instance of Arrow this is just a pure function; in general, however, an arrow may represent some sort of “effectful” computation.

  // Having an Arrow instance for a type constructor F[_, _] means that an F[_, _] can be composed and combined with other F[_, _]s.
  // You will be able to do things like:
  //
  // - Lifting a function ab: A => B into arrow F[A, B] with Arrow[F].lift(ab). If F is Function1 then A => B is the same as F[A, B] so lift is
  // just the identity function.
  // - Composing fab: F[A, B] and fbc: F[B, C] into fac: F[A, C] with Arrow[F].compose(fbc, fab), or fab >>> fbc. If F is Function1 then >>> becomes
  // an alias for andThen.
  // - Taking two arrows fab: F[A, B] and fcd: F[C, D] and combining them into F[(A, C) => (B, D)] with fab.split(fcd) or fab *** fcd. The resulting
  // arrow takes two inputs and processes them with two arrows, one for each input.
  // - Taking an arrow fab: F[A, B] and turning it into F[(A, C), (B, C)] with fab.first. The resulting arrow takes two inputs, processes the first
  // input and leaves the second input as it is. A similar method, fab.second, turns F[A, B] into F[(C, A), (C, B)].

  // Example 1 - Combining Arrows
  // We define a combine function that combines two arrows into a single arrow, which takes an input and processes two copies of it with two
  // arrows. combine can be defined in terms of Arrow operations lift, >>> and ***:

  def combine[F[_, _]: Arrow, A, B, C](fab: F[A, B], fac: F[A, C]): F[A, (B, C)] =
    Arrow[F].lift((a: A) => (a, a)) >>> (fab *** fac)

  // Example 2 - Kleisli
  // A Kleisli[F[_], A, B] represents a function A => F[B]. You cannot directly compose an A => F[B] with a B => F[C] with functional composition,
  // since the codomain of the first function is F[B] while the domain of the second function is B; however, since Kleisli is an arrow
  // (as long as F is a monad), you can easily compose Kleisli[F[_], A, B] with Kleisli[F[_], B, C] using Arrow operations.

  // Suppose you want to take a List[Int], and return the sum of the first and the last element (if exists). To do so, we can create two
  // Kleislis that find the headOption and lastOption of a List[Int], respectively:
  val headK: Kleisli[Option, List[Int], Int] = Kleisli((_: List[Int]).headOption)
  val lastK: Kleisli[Option, List[Int], Int] = Kleisli((_: List[Int]).lastOption)

  //  With headK and lastK, we can obtain the Kleisli arrow we want by combining them, and composing it with _ + _:
  val headPlusLast = combine(headK, lastK) >>> Arrow[Kleisli[Option, ?, ?]]
    .lift(((_: Int) + (_: Int)).tupled)

  headPlusLast.run(List(2, 3, 5, 8))
  // res1: Option[Int] = Some(10)

  headPlusLast.run(Nil)
  // res2: Option[Int] = None
}
