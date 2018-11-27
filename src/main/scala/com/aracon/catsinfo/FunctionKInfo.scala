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
import cats.arrow.FunctionK

object FunctionKInfo {

  // A FunctionK transforms values from one first-order-kinded type (a type that takes a single type parameter, such as List or Option) into another first-order-kinded type.
  // This transformation is universal, meaning that a FunctionK[List, Option] will translate all List[A] values into an Option[A] value for all possible types of A

  // For example we can transform all List[A] to Option[A] using
  val first: List ~> Option = new ~>[List, Option] {
    override def apply[A](fa: List[A]): Option[A] = fa.headOption
  }

  // That is similar to a Natural transformation, and in fact we can implement a natural transformation using FunctionK
  def natTrans[F[_], G[_]]: FunctionK[F, G] = new ~>[F, G] {
    override def apply[A](fa: F[A]): G[A] =
      ??? // usually we need a bit more info on F and G to be able to do the conversion :)
  }
  // but there are theoretical differences between proper natural transformation and what FunctionK provides.

  // In any cas, FunctionK allows us to move between first-order-kinded type which can be very handy in Tagless final style coding
  // where we work with these types often.

}
