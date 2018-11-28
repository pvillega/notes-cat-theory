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
import cats.implicits._

object ParallelInfo {
  // Parallel abstracts over Monads which are also capable of forming an Applicative that supports parallel composition.
  // For example, Either (Monad) and Validated (Applicative) have this kind of relation.

  // An implementation is based on that relation between a Monad and the corresponding Applicative:
  trait MyParallel[M[_], F[_]] {
    def sequential: F ~> M

    def parallel: M ~> F
  }
  // where M[_] is a Monad and F[_] is an Applicative
  // This enables us to use one or the other (Monad or Applicative) as the underlying layer for an operation
  // Parallel provides several helper methods, like `mapN` or `parSequence` that take advantadge of that to run operation in parallel when possible.

  // For example, as we have a Parallel instance for Either, we can run this map using teh underlying Applicative, in parallel,
  // instead of using the sequential computation enforced by the monad. This would help with long running validations/checks, for example
  (1.asRight[String], 2.asRight[String]).parMapN { case (x, y) ⇒ x * y }

  // For types which cannot form Monads or Applicatives as they can't define `pure`, cats provides `NonEmptyParallel`
  type SampleType[M[_], F[_]] = NonEmptyParallel[M, F]
  // NonEmptyParallel depends on M[_] having a FlatMap and F[_] having an Apply, that is, the parents of Monad and Applicative in the hierarchy,
  // which don't require `pure`
  // In exchange we lose some methods, like `parTraverse` or `parSequence`

}
