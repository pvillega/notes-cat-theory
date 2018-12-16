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

package com.aracon.monocle

import monocle.Prism

// http://julien-truffaut.github.io/Monocle/optics/prism.html
object PrismInfo {
  // A Prism is an optic used to select part of a Sum type (also known as Coproduct), e.g. sealed trait or Enum.
  // This is the difference with a Lens. Lenses focus on elements in a single Product (nested case classes, etc)
  // while Prism works with the Coproducts in an ADT. You can use both to make specific changes to an elements of an
  // ADT by first navigating the value with Prism and then with Lens.

  // Prisms have two type parameters generally called S and A: Prism[S, A] where S represents the Sum and A a part of the Sum.
  // It has the following operations:
  // - getOption: S => Option[A]  -> note this is an option as the Coproduct may not have that value available!
  // - reverseGet (aka apply): A => S
  // - set : modifies the A, only if the A exists. If `getOption` would be `None` this becomes a no-op
  // - modify: equivalent to `set`
  // - setOption: like `set` but returns `None` if the operation failed
  // - modifyOption: equivalent to `setOption`

  // For example given:
  sealed trait Json
  case class JStr(v: String) extends Json
  case class JNum(v: Double) extends Json

  // We can define a Prism to get only Strings
  val jStr = Prism[Json, String] {
    case JStr(v) => Some(v)
    case _       => None
  }(JStr)

  // Also commonly defined via this method:
  val jStr2 = Prism.partial[Json, String] { case JStr(v) => v }(JStr)

  // As we use the Prism, we will get the value or None accordingly
  jStr.getOption(JStr("Hello"))
  // res2: Option[String] = Some(Hello)

  jStr.getOption(JNum(3.2))
  // res3: Option[String] = None
}
