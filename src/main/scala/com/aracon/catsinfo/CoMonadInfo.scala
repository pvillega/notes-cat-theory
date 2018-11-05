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
    def counit[A](fa: F[A]): A // aka extract
    def cojoin[A](fa: F[A]): F[F[A]]

    def coflatMap[A, B](fa: F[A])(f: F[A] => B)(implicit F: Functor[F]): F[B] =
      F.map(cojoin(fa))(f)
  }

  // One of the simplest examples of a comonad is an infinite stream of data.
  // Comonads crop up anywhere where we want to extend a computation that is local to a small part of a data structure to the full data structure.


}
