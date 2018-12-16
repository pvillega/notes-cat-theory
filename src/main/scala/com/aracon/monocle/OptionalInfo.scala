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

import monocle.Optional

// http://julien-truffaut.github.io/Monocle/optics/optional.html
object OptionalInfo {
  // Optional is similar to a Lens, but with the caveat that the element may not exist
  // Optionals have two type parameters generally called S and A: Optional[S, A] where S represents the Product and A an optional element inside of S
  // For example, if we have an Optional from a List[A] to A, an empty list wouldn't have the element
  // Operations:
  // - getOption : Returns an Option[A] where None means the element doesn't exist
  // - set : Given an A and S returns an S with given A
  // - nonEmpty: returns a boolean indicating if there is a match for the given Optional

  // Example of an Optional that can retrieve the head element of a list (if existing) and can replace it via `set`:
  val head = Optional[List[Int], Int] {
    case Nil     => None
    case x :: xs => Some(x)
  } { a =>
    {
      case Nil     => Nil
      case x :: xs => a :: xs
    }
  }

  head.getOption(Nil) // None

}
