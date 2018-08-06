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

object FlatMapInfo {
  // FlatMap is the typeclass implementing known 'Monad' methods as 'flatten' and 'flatMap'

  // Operation 'flatten' allow us to simplify nested F[_]. When working with F[_] and its contents
  // is not unusual to end up with F[F[_]]. Flatten reduces the F[_] to a single Effect.

  // Examples:
  FlatMap[Option].flatten(1.some.some) // Some(1)
  FlatMap[List].flatten(List(List(1))) // List(1)

  // Operation 'flatMap' allows us to sequence operations in the F[_]
  // The definition:
  //        def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  // show that we can use the contents of F[A] to generate a new F[B]. This implies we need a full
  // F[A] before we can call 'f', creating a definite sequence in the Effects.
  // See MonadInfo for more details

  // Examples:
  val strToList: String ⇒ List[Char] = (x: String) => x.toCharArray.toList
  FlatMap[List].flatMap(List.empty)(strToList)     // List()
  FlatMap[List].flatMap(List("Hello!"))(strToList) // List(H, e, l, l, o, !)
}
