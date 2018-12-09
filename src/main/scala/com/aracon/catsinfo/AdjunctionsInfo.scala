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

object AdjunctionsInfo {

  // Adjunctions are among the most important constructions in mathematics.
  // - First, every fundamental datatype— sums, products, function types, recursive types—arises out of an adjunction.
  //   The defining properties of an adjunction give rise to well-known laws of the algebra of programming.
  // - Second, adjunctions are instrumental in unifying and generalising recursion schemes.

  // They are present on many structures, as can be seen at https://www.youtube.com/watch?v=h60VMBPfLoM
  // But currently it seems to be more of a theoretical foundation that helps generating other constructions
  // than a typeclass we can directly apply.
  //
  // Even if we would us Adjunctions to build, for example, our own State monads, it would be much less performant than
  // the State monad implementations we currently use. So, good for lawfulness, theoretical basis of constructs, but
  // not really applicable right now.
}
