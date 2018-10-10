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

object FunctorInfo {
  // Functor of A - F[A] - class that encapsulates sequencing computa􏰀tions via method 'map', you
  // can think of it as 'appending computations to a chain'
  // A functor allows us to 'peek inside a wrapper type' and modify its content via computations,
  // in a specific order, without having to specifically unwrap it.
  //
  // On some FP constructs we may want to leave F abstract so we can reuse the code easily, but
  // this means we can't unwrap the type to operate with the contents, so without 'map' we
  // wouldn't be able to modify it
  //
  // Functions *are* functors, which means we can map over them! Which is same as composition
  val func1: Int => Double    = (x: Int) => x.toDouble
  val func2: Double => Double = (y: Double) => y * 2
  (func1.map(func2))(1)    // composition using map, returns 2.0
  (func1 andThen func2)(1) // composition using andThen, returns 2.0
  func2(func1(1))          // composition written out by hand, returns 2.0

  // Functors compose! Very important. You can avoid ugly code like:
  val uglyList: List[Option[Int]] = List(1.some, none[Int], 3.some, 4.some)
  def uglyMap: List[Option[Int]]  = uglyList.map(_.map(_ * 2))
  // by taking advantage of Functor composition, we can build more readable code, like:
  def properMap: List[Option[Int]] = Functor[List].compose[Option].map(uglyList)(_ * 2)

  // Functors can lift functions so we can apply them to a Functor's content. For example given:
  def lenght(s: String): Int = s.length
  val myVal                  = Some("value")
  // we can't apply 'length' directly to 'myVal' as types don't match. But we can lift the function as follows:
  val newLenght: Option[String] ⇒ Option[Int] = Functor[Option].lift(lenght)
  newLenght(myVal)
  // Lifting wraps input and output parameters in F[_] so we cna now use our F[_] as input.
  // What if we have multiple parameters in the function to lift? See Applicative.

  // Contravariant Functor, provides an operati􏰀on called contramap that represents “prepending”
  // an operati􏰀on to a chain.
  // Contramap method only makes sense for data types that represent transformati􏰂ons. Option has
  // no contramap because there is no way of feeding a value in an Option[B] backwards through a
  // functi􏰀on B => A.
  // For example printable:
  trait Printable[A] {
    def format(value: A): String

    def contramap[B](func: B => A): Printable[B] =
      (value: B) => Printable.this.format(func(value))
  }
  // In this example contramap helps us define a new Printable given an existing Printable
  // and a function. To make it more clear, think about this definition:
  case class Box[A](value: A)
  implicit def printBox[A](implicit p: Printable[A]): Printable[Box[A]] =
    p.contramap[Box[A]](_.value)
  // we use an existing Printable[A] in context (implicit) to then generate a new
  // 'Printable[Box[A]]' via 'contramap' instead of manually defining the full type class

  // Invariant functors implement a method called imap that is informally equivalent to a
  // combinati􏰀on of map and contramap. Method 'imap' generates values via a pair of
  // bidirecti􏰀onal transforma􏰀tions.
  // The most intui􏰀ve examples of this are a type class that represents encoding and decoding
  // as some data type, like Json encoders/decoders
  trait Codec[A] { self ⇒
    def encode(value: A): String
    def decode(value: String): A
    def imap[B](dec: A => B, enc: B => A): Codec[B] = new Codec[B] {
      def encode(value: B): String = self.encode(enc(value))
      def decode(value: String): B = dec(self.decode(value))
    }
  }

  // To see usefulness of 'imap', similar to 'contramap' we can build new type classes using this
  // method, avoiding full definition. See following Codec[Int] built using Codec[String]
  implicit val stringCodec: Codec[String] =
    new Codec[String] {
      def encode(value: String): String = value
      def decode(value: String): String = value
    }

  implicit val intCodec: Codec[Int] = stringCodec.imap(_.toInt, _.toString)
}
