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

import monocle.Iso

// http://julien-truffaut.github.io/Monocle/optics/iso.html
object IsoInfo {
  // An Iso converts elements from type A into elements from type B without losing data
  // For example, List to Vector, to a Product to a Tuple
  // It has 2 operations: `get` does the conversion one way, `reverseGet` the other way

  // Example:
  case class Person(name: String, age: Int)

  val personToTuple: Iso[Person, (String, Int)] = Iso[Person, (String, Int)](p => (p.name, p.age)) {
    case (name, age) => Person(name, age)
  }
  personToTuple.get(Person("Zoe", 25))
  personToTuple.reverseGet(("Zoe", 25))
}
