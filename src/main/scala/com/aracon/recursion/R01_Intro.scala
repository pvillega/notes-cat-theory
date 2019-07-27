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

package com.aracon.recursion

import higherkindness.droste._
import higherkindness.droste.macros.deriveFixedPoint

// See https://www.47deg.com/blog/basic-recursion-schemes-in-scala/
// Recursion schemes are very relevant to FP, as they provide generalized folds, unfolds, and traversals for
// fixed point data structures.
// The main value is separation of concerns: we don't care on how the recursion is done, we care about how to operate
// with our values, and recursion schemes provide the recursion 'for free'. Simpler code, better designed.

// Let's start by defining a well known recursive type
sealed trait List
case object Nil                        extends List
case class Cons(head: Int, tail: List) extends List

// It is recursive as `Cons` references `List` as a parameter.
// We can `fold` a list, which is basically a recursive operation over its elements
// We can also `unfold` a list, that is, we could take an integer and use a sequence of recursive operations which generate
// a new element for a list on each step.

// These 2 functions can be seen both as complementary (one builds a list, one destroys the list) and also as functions that
// are composed of a single-step function repeated a number of times (like a loop)

// `fold` is also called `catamorphism`, as it transforms an algebra as follows: Rec[A] => A
// `unfold` is also called `anamorphism`, as it builds our algebra from a base type: A => Rec[A]

// A Fixed point data structure (Fixpoint Types) captures the recursive application of a generic type constructor Rec[_]

// The simples possible, as implemented by Matryoshka, is `Fix`, defined as:
//    final case class Fix[F[_]](unFix: F[Fix[F]])

// in Droste the equivalent isimplemented as an obscured alias:
//    type Fix[F[_]] = F[Fix[F]]

// To work with recursion schemes in your recursive data structure, you just follow a few easy steps.
// Assuming `List` above, we first replace the recursive parameters by a generic type parameter `A`:
// Any leaf nodes can be safely be left as A == Nothing. You also need a functor for your type.
// This can be done automatically with a macro, as below:

// See:
@deriveFixedPoint sealed trait RList
object RList {
  final case class Dummy()

  final case class RNil()                        extends RList
  final case class RCons(head: Int, tail: RList) extends RList
}

// Then you can operate using Fix point types.
object R01_Intro {
  import RList._
  import RList.fixedpoint._

  // we can now call `catamorphism` on the fix point recursive type, to fold over it and get an String
  val algebra: Algebra[RListF, String] = Algebra {
    case RNilF()      => "nil"
    case RConsF(h, t) => s"$h :: $t"
  }
  // or we can build a coalgebra for anamorphisms to generate a list from the input (in this case, a String)
  val coalgebra: Coalgebra[RListF, String] = Coalgebra { str ⇒
    str.headOption.map(c ⇒ RConsF(c.toString.toInt, str.tail)).getOrElse(RNilF())
  }
  val evaluate: RList ⇒ String = scheme.cata(algebra)
  val construct                = scheme.ana(coalgebra)

  def main(args: Array[String]): Unit = {
    println(evaluate(RCons(1, RCons(2, RNil()))))
    println(construct("1234"))
  }
}
