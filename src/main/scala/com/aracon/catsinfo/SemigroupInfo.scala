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

object SemigroupInfo {

  // A semigroup is just the combine part of a Monoid (see MonoidInfo). While many semigroups
  // are also monoids, there are some data types for which we cannot define an empty element.
  //
  // For example 'NonEmptyList' has no 'empty' element by definition, so it violated the laws
  // regarding 'empty' for Monoid (can't be implemented) but it has a lawful associative
  // combine operation. A Semigroup.

  // Example:
  Semigroup[String].combine("Hi ", "there") //"Hi there"

  // Semigroup provides special syntax for Combine:
  val intResult: Int = 1 |+| 2 // 3. Remember SemiGroup has NO 'empty'!

}
