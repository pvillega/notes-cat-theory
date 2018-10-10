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

object FoldableInfo {

  // Typeclass that provides foldRight and foldLeft operations, as well as plenty of other useful ones
  // like find, exists, forall, toList, isEmpty, nonEmpty, and so on
  // Useful with Monoids and Eval monads

  // Examples:
  Foldable[List].foldLeft(List(1, 2, 3), 0)((acc, n) ⇒ acc + n) // 6

  // combineAll uses a Monoid for the type to combine all elements of a sequence
  Foldable[List].combineAll(List(1, 2, 3)) // 6

  // foldMap applies a provided function to the elements (map) and combines the results using an implicit Monoid for the type
  Foldable[List].foldMap(List(1, 2, 3))(_ + 1) //9

  // we can compose Foldables to support deep traversal of nested sequences:
  val ints = List(Vector(1, 2, 3), Vector(4, 5, 6))
  (Foldable[List] compose Foldable[Vector]).combineAll(ints) //21
}
