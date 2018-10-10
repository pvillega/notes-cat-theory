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

package com.aracon.modeling

object AlgebraModelingInfo {

  // Modeling with Abstract Algebra is all about finding the properties that our domain holds.
  // Properties = specific values irrelevant, we care about properties when viewing inputs and outputs to our domain
  // at a more abstract level.

  // Closure — Given two elements of the same type, we combine them to produce another element of the same type. 1 + 2 = 3 "hello " + "world" = "hello world"
  // Associativity — We can place parenthesis anywhere we’d like in a sequence of combinations (1 + 2) + 3 = 1 + (2 + 3) ("a" + "b") + "c" = "a" + ("b" + "c")
  // Identity — There is a special value we can apply that has no affect on the outcome 0 + n = n = n + 0
  // Commutativity — The order of the elements doesn’t matter 1 + 2 = 2 + 1
  // Idempotence — Receiving the same message multiple times has no affect after the first time Math.max(1, 2) = Math.max(Math.max(1, 2), 2)

}
