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

import cats._
import cats.data.Kleisli
import cats.implicits._

class KleisliInfo {
  // A Kleisli[F[_], A, B] represents a function A => F[B]. It allows us to compose
  // operations that return F[_] without having to pass the F itself as a parameter.

  // Example (taken from Cats documentation):
  val parse: String => Option[Int] =
    s => if (s.matches("-?[0-9]+")) Some(s.toInt) else None

  val reciprocal: Int => Option[Double] =
    i => if (i != 0) Some(1.0 / i) else None

  // We can't compose the functions above with `andThen`, we'd need to flatMap over the results. But, using Kelisli, we can do:
  val parseKleisli      = Kleisli(parse)
  val reciprocalKleisli = Kleisli(reciprocal)
  val parseAndReciprocal: Kleisli[Option, String, Double] =
    reciprocalKleisli.compose(parseKleisli)

  // Kleisli accepts F[_] which are Functors, Applicatives, or Monads. Different operations in Kleisli have different constrains on F,
  // so we can't compose Kleisli unless F is a Monad, but we can still `map` over Kleisli if F is a Functor.

  // Kleisli can be viewed as the monad transformer for functions, as `Reader` in implemented in terms of Kleisli and `Reader` is basically
  // an A => B
  type Reader[A, B] = Kleisli[Id, A, B] // A => Id[B] and Id[B] == B

}
