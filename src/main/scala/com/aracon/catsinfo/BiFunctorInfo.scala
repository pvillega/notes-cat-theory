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

object BiFunctorInfo {

  // Bifunctor is a Functor with '2 type parameters', that is: F[A, B]
  // It provides a `bimap` method that allows us to work with both sides at the same time.
  def bimap[F[_, _], A, B, C, D](fab: F[A, B])(f: A => C, g: B => D): F[C, D] = ???

  // Also provides a `leftMap` method for convenience:
  def leftMap[F[_, _], A, B, C](fab: F[A, B])(f: A => C): F[C, B] = bimap(fab)(f, identity)

  // As all Functors, BiFunctors compose. Either is an example of a BiFunctor. Tuple2 is another.

  // Similar to BiFunctors are ProFunctors. They provide a method `dimap` which differs from `bimap` on the `f` passed to it:
  def dimap[F[_, _], A, B, C, D](fab: F[A, B])(f: C => A, g: B => D): F[C, D] = ???

  // ProFunctors describe generalised functions as they describe any structure that consumes values (A) and produces values (B) without caring about context.
  // Examples:
  type Output[A, B] = A ⇒ List[B]
  type Input[A, B]  = Option[A] ⇒ B
  type Both[A, B]   = Option[A] ⇒ List[B]

  // ProFunctors let us forget about input and output effects, hiding them.

}
