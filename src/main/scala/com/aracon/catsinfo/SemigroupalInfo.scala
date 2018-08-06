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

object SemigroupalInfo {

  // Not to be confused with Semigroup
  // Semigroupal provides a 'product' operation that does Cartesian products with the elements in our F[_]
  // That is, given F[A] and F[B] you get F[(A, B)]

  // Examples:
  Semigroupal[Option].product(1.some, 2.some) // Some((1, 2))
  Semigroupal[Option].product(1.some, none)   // None

  Semigroupal[List].product(List(1, 2), List(3, 4)) // List((1,3), (1,4), (2,3), (2,4))
  Semigroupal[List].product(List.empty, List(3, 4)) // List()

}
