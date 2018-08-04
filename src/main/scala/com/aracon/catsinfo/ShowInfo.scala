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

import java.util.Date

import cats._

object ShowInfo {

  // Most type classes provide helper methods to construct new instances for that type class
  // For example, with Show[A] we have:
  implicit def helper1: Show[Date] = Show.show[Date](date ⇒ s"${date.getTime}ms since epoch")

  // or a way to reuse default 'toString' in Scala if that is good enough for us
  implicit def helper2: Show[Long] = Show.fromToString[Long]

}
