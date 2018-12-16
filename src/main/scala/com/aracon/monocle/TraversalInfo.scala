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
import monocle.Traversal

// http://julien-truffaut.github.io/Monocle/optics/traversal.html
object TraversalInfo {
  // Traversal generalises Optional, to affect several elements at once
  // In other word, a Traversal allows to focus from a type S into 0 to n values of type A
  // Examples include focusing on all the elements in a List, or extracting more than 1 element from a Product

  // Operations:
  // - set : Given an A, updates all elements in S selected by the Traversal to that value. A list could be initialised to a base value with this.
  // - modify: get and set on the given S
  // It also inherits the operations of `Fold` like `getAll`, `find`, and `all`

  // Example with Product, we retrieve 2 elements as a Tuple at once:
  case class Point(id: String, x: Int, y: Int)

  // method `apply2` is a helper for when you want to extract 2 elements at once
  val points = Traversal.apply2[Point, Int](_.x, _.y)((x, y, p) => p.copy(x = x, y = y))

  points.set(5)(Point("bottom-left", 0, 0)) // Point(bottom-left,5,5)
}
