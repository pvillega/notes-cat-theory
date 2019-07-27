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

object MonoidInfo {

  // A Monoid for type A is:
  //   * a combine operations that given 2 elements of type A retuns a type A
  //   * an empty A element such that combine(A,empty) == combine(empty,A) == A
  //
  // See Cats implementation:
  //
  // trait Monoid[A] {
  //  def combine(x: A, y: A): A
  //  def empty: A
  //}
  //
  // LAWS:
  // Monoid is operations + laws. Laws expect combine to be associative, and empty to be
  // an identity element (combine(A,empty) == combine(empty,A) == A)
  // Type A with operations but without laws is not a real Monoid, although it could seem one
  //
  // To reiterate, a monoid over a type is a set of operations + laws. We use a type class to
  // represent it, but the value is on operations + laws
  //
  // Why Laws:
  // Cats library expects lawful monoids. If we implement a Monoid that is not lawful we risk
  // unexpected behaviour in our app.
  // For example, substraction is not associative. Think on Map/Reduce (break, calculate, join)
  // If we create a map-reduce that uses a monoid for the 'reduce' part but then we use a Monoid
  // using substraction (which is not associative) we will get inconsistent results each execution
  // as reduce calls are made out of order. Thus, laws matter.

  // We can invoke monoid instances directly via apply, as usual with Cats:
  Monoid[String].combine("Hi ", "there")         // "Hi there"
  Monoid[String].empty                           // ""
  Monoid[String].isEmpty("")                     // true
  Monoid[Option[Int]].combine(1.some, none[Int]) // Some(1)

  // Monoid benefits from special syntax for combine provided by Semigroup as it inherits from it
  val intResult: Int = 1 |+| 2 |+| Monoid[Int].empty // 3

}
