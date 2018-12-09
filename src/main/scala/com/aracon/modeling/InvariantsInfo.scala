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

object InvariantsInfo {
  // An invariant is a requirement that must hold for the data to have meaning. How to enforce them in Scala?

  // If we have an ADT (NOTE: don't leave out the `extends Product with Serializable` part or all this won't work due to type inference!)
  sealed trait Parent extends Product with Serializable
  case object A       extends Parent
  case object B       extends Parent
  case object C       extends Parent

  // and we want to implement the following method where we want to enforce as an invariant that `one` and `two` have the same type.
  def bothTypesSame[Type <: Parent](one: Type, two: Type) = ???

  // Unfortunately this is possible:
  bothTypesSame(A, B) // Compiles - both parameters are reduced to type `Parent`

  // How to enforce it via code? If ti was a class we could play by hiding the public constructor and providing validation logic
  // on Apply, but that would only work at runtime anyway. And doesn't work for functions.

  // We can use generalized type constrains to enforce this at compile time, via Shapeless
  import shapeless._
  def bothTypesSameForReal[Type <: Parent](one: Type, two: Type)(implicit ev: Type =:!= Parent) =
    ???

  // What we enforce with the implicit is a condition that `Type` and `Parent` can't be the same type. Which means we must use a
  // subtype of `Parent` as `Type`, which will enforce the fact that `one` and `two` have exactly the same type:

  // bothTypesSameForReal(A, B) // Fails compilation
  bothTypesSameForReal(A, A) // Compiles

  // We can't use an invariant (ev) to enforce both `Type` must be the same, as we only use a single `Type` parameter. We could do this alternative implementation though:
  def bothTypesAlsoForReal[Type1 <: Parent, Type2 <: Parent](one: Type1, two: Type2)(
      implicit ev: Type1 =:= Type2
  ) =
    ???

  bothTypesAlsoForReal(A, B) // Fails compilation
  bothTypesAlsoForReal(A, A) // Compiles
}
