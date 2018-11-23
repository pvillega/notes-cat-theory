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

object CoMonadInfo {

  // CoMonad is the dual (reverse the arrows) of a Monad
  trait Monad[F[_]] {
    def unit[A](a: A): F[A]
    def join[A](ffa: F[F[A]]): F[A]

    def flatMap[A, B](fa: F[A])(f: A => F[B])(implicit F: Functor[F]): F[B] =
      join(F.map(fa)(f))
  }

  trait Comonad[F[_]] {
    def counit[A](fa: F[A]): A       // aka extract
    def cojoin[A](fa: F[A]): F[F[A]] // aka duplicate

    def coflatMap[A, B](fa: F[A])(f: F[A] => B)(implicit F: Functor[F]): F[B] = // aka extend
      F.map(cojoin(fa))(f)
  }

  // One of the simplest examples of a comonad is an infinite stream of data.
  // Comonads crop up anywhere where we want to extend a computation that is local to a small part of a data structure to the full data structure.
  // See:
  // - http://blog.higher-order.com/blog/2015/06/23/a-scala-comonad-tutorial/

  // A common example across many posts for a Comonad is a Zipper class that has a focus on an element inside a stream, and allows us to
  // navigate left or right of that element
  case class StreamZipper[A](left: Stream[A], focus: A, right: Stream[A]) {
    // note elements in `left` are in reverse order to simplify implementation
    def moveLeft: StreamZipper[A] =
      new StreamZipper[A](left.tail, left.head, focus #:: right)

    def moveRight: StreamZipper[A] =
      new StreamZipper[A](focus #:: left, right.head, right.tail)

    // A stream of zippers, with the focus set to each element on the left
    private lazy val lefts: Stream[StreamZipper[A]] =
      Stream.iterate(moveLeft)(_.moveLeft).zip(left.tail).map(_._1)

    // A stream of zippers, with the focus set to each element on the right
    private lazy val rights: Stream[StreamZipper[A]] =
      Stream.iterate(moveRight)(_.moveRight).zip(right.tail).map(_._1)

    lazy val cojoin: StreamZipper[StreamZipper[A]] =
      new StreamZipper[StreamZipper[A]](lefts, this, rights)
  }

  implicit object ZipperComonad extends Comonad[StreamZipper] {
    // counit focuses on one element of the F, in this case the `focus` of the Zipper. Thus the other name, `extract`
    def counit[A](fa: StreamZipper[A]): A =
      fa.focus

    // for `cojoin` the key insight here is that we want to generate a StreamZipper where each element has the same elements as the initial
    // StreamZipper but with the focus shifted. It extends the structure, so that for every element in the original structure, there is a copy
    // of the structure with the focus on the corresponding element. Thus the other name, `duplicate`
    def cojoin[A](fa: StreamZipper[A]): StreamZipper[StreamZipper[A]] =
      fa.cojoin

    // for `coflatmap` we can use the default implementation in the Comonad. `coflatMap` allows us to extend a local computation to apply
    // in a global context. Thus the other name, `extend`. We use that to calculate new focus points in the structure that we can extract later
    // on.
    override def coflatMap[A, B](
        fa: StreamZipper[A]
    )(f: StreamZipper[A] => B)(implicit F: Functor[StreamZipper]): StreamZipper[B] =
      F.map(cojoin(fa))(f)
  }

  // We add a Functor as we may need it for some examples below
  implicit object ZipperFunctor extends Functor[StreamZipper] {
    override def map[A, B](fa: StreamZipper[A])(
        f: A ⇒ B
    ): StreamZipper[B] = StreamZipper(fa.left.map(f), f(fa.focus), fa.right.map(f))
  }

  // An example of a Comonad use to generate new data inside a F taken from:
  // - https://dodisturb.me/posts/2018-01-28-Cellular-Christmas-Tree.html
  // is to make a Christmas tree. We only implement the core functions in here, we skip the blinking part
  // We use the StreamZipper class directly instead of the Comonad, but you can see implementations are equivalent.

  // Define the start point, top of the tree
  def initConf: StreamZipper[Int] = StreamZipper(Stream.continually(0), 1, Stream.continually(0))

  // We want to grow the tree, changing the value of the focus of each level as per the previous level
  // Technically, an Xor of the left and right elements. Notice this matches the signature of `extract`!
  def grow(input: StreamZipper[Int]): Int =
    if (input.left == input.right) 0 else 1

  // Generates a set of Zippers with the focus shifted, using the `cojoin` operation. In effect this creates multiple
  // levels for the tree. Then we use `map` and `grow` to differentiate the values on the focus for each level
  def trees(input: StreamZipper[Int])(implicit CM: Comonad[StreamZipper],
                                      F: Functor[StreamZipper]): StreamZipper[StreamZipper[Int]] =
    F.map(input.cojoin)(CM.coflatMap(_)(grow))

  // Only for visual clarity. We will have only 0 and 1 in the tree. We print 0 as whitespace. The blinking tree has more options
  def display(i: Int): Char = if (i == 0) ' ' else '*'

  // Only for visual clarity, we select a subset of the streams in the zipper for printing purposes
  def frame(widht: Int, height: Int, zipper: StreamZipper[Int]): List[Int] =
    zipper.left.take(widht - 1).toList ++ (zipper.focus :: zipper.right.take(widht - 1).toList)

  // we could now, in theory, print a tree using:
  val F                                      = implicitly[Functor[StreamZipper]]
  val tree: StreamZipper[StreamZipper[Int]]  = trees(initConf)
  val selectToPrint: StreamZipper[List[Int]] = F.map(tree)(frame(16, 17, _))
  F.map(selectToPrint) { list ⇒
    println(list.map(display))
  }
}
