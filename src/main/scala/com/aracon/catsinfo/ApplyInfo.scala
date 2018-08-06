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

object ApplyInfo {
  // Apply provides a way to operate with lifted functions in an F[_] context
  // This is done with method 'ap' which takes a lifted operation and an F[_] to apply the function inside the F[_]
  //
  // Why do we need this method? Often we will be working in the context of F[_]. As we discussed with Functor,
  // operations that work directly with the contents inside the F[_] allows us to run computations without having
  // to open the F[_], which we may not know how to do as we are in an abstract context.
  //
  // The main difference between 'ap' and 'map' is that 'ap' can work in parallel. This is not relevant when we have 1
  // single lifted function but, as can be seen in Applicative, we can use it with multiple lifted functions at once,
  // enabling parallelism.

  // Example:
  val isGt2: List[Int => Boolean] = List(_ > 2)
  Apply[List].ap(isGt2)(List(0, 1, 2, 3, 4)) // List(false, false, false, true, true)
}
