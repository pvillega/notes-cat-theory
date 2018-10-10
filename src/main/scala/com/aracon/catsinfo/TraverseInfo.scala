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

object TraverseInfo {

  // Traverse type class is a higher level tool that leverages Applicatives to provide a more convenient, more lawful, pa􏰁ttern for itera􏰀tion.

  // traverse method is defined as
  def traverse[F[_], G[_]: Applicative, A, B](inputs: F[A])(func: A => G[B]): G[F[B]] = ???
  // it iterates over an F type, applying a f : A => G[B] to each element. In the end we get a G[F[B]].
  // That is, we can for exemple apply to a List a function call that returns a Future, to accumulate all the operations in a single Future[List]

  // sequence is equivalent to traverse where we do no mapping, so it simply swaps the effects, turning the inner effect into the outer one
  def sequence[F[_], G[_]: Applicative, B](inputs: F[G[B]]): G[F[B]] = traverse(inputs)(identity)
}
