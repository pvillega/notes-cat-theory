package com.aracon.catsinfo

import cats._
import cats.data.Reader
import cats.implicits._

import scala.util.Try

object MonadInfo {
  // Monad - A monad is a mechanism for sequencing computa􏰁tions (F[_])
  // A Functor only allows 1 computation at the beginning of the sequence and then transforms its contents.
  // A Monad takes in account intermediate F[_] in the sequence, allowing us to chain multiple computations with a
  // context

  // Monadic behaviour is abstracted in 2 operations:
  // pure, of type A => F[A]
  // flatMap, of type (F[A], A => F[B]) => F[B]
  //
  // Simplified example:
  trait Monad[F[_]] {
    def pure[A](value: A): F[A]
    def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
  }
  // Both operations must obey a set of laws, which allow us to sequence operations (left identity, right identity,
  // associativity). If a Monad doesn't obey the laws, we may have unexpected errors and side effects.

  // Monad inherits from Applicative (which inherits from Functor) and from FlatMap. This means Monad has methods
  // like 'map' or 'ap' or 'compose' available.
  Monad[Option].compose[List].map(Option(List(1,2)))(a ⇒ a + 1) // Option(List(2,3))

  // Useful Monads:
  // Id -  Id allows us to call our monadic method using plain values. It's a type (Id[A]) for which Cats provides
  // several type classes like Functor[Id] or Monad[Id]

  // Either - A monad with two sides (Either[E,T]) used commonly to manage errors. Either makes the decision that the
  // right side represents the success case and thus supports map and flatMap directly. Note this is a convention, we can
  // use it to just carry 2 values around
  // Cats provides useful methods for Either
  1.asRight[String] // Preferred over Right() for type inference reasons
  Either.catchNonFatal(1)
  Either.catchOnly[RuntimeException](1)
  Either.fromOption(Option(1), "Left")
  Either.fromTry(Try(1))
  // Some other Either utils to validate data
  (-1).asRight[String].ensure("Must be non-negative!")(_ > 0) // Either[String, Int] -> Returns: Left[String]
  // And methods to 'recover' from an error if the Either we receive is a left
  "error".asLeft[Int].recover {
    case _: String => -1
  }
  "error".asLeft[Int].recoverWith {
    case _: String => Right(-1)
  }


  // MonadError - MonadError that abstracts over Either-like data types that are used for error handling. MonadError
  // provides extra opera􏰀ons for raising and handling errors.
  // Examples:
  type ErrorOr[A] = Either[String, A]
  val monadError: MonadError[ErrorOr, String] = MonadError[ErrorOr, String]

  val error = monadError.raiseError("error") // Left[String]
  monadError.handleError(error)(_ ⇒ "handled") // for error recovery. Had a monadic 'handleErrorWith' equivalent
  monadError.ensure(monadError.pure(1))("Number too low!")(_ > 1000) // Returns LEft[String], predicate failed to verify

  // Eval - Eval is a monad for memoization of results. It has 3 subtypes: Now, Always, Later
  // Now - eagerly evaluates operation and memoizes result, like a 'val'
  // Always - lazy and non memoized computation, like a 'def' in scala
  // Later - lazy and memoized computation, like a 'lazy val'
  // When you call .eval in an Eval[] all the intermediate operations are run, as per behaviour defined above
  val greeting: Eval[String] = Eval.
    always { println("Step 1"); "Hello" }.
    map { str => println("Step 2"); s"$str world" }
  greeting.value
  // Eval has a 'memoize' method that allows us to memoize a chain of computati􏰀ons. The result of the chain up to the
  // call is cached, while later operations revert to their own behaviour
  val greeting: Eval[String] = Eval.
    always { println("Step 1"); "Hello" }.
    memoize.
    map { str => println("Step 2"); s"$str world" }
  // Eval has a 'defer' method that allows us to delay execution of a function until it is requested. Is like a Lazy
  // but it takes an Eval as parameter, so we can help managing stack/recursion in methods.


  // Writer - Writer is a monad that lets us carry a log along with a computati􏰀on.
  // A common use case is to store logs of threaded computations so each computation contains the logs and we get the right
  // order of events in the logs

  // Reader - Reader is a monad that allows us to sequence opera􏰀tions that depend on some input.
  // A common use is dependency injection. We can define a set of operations depending on a value,
  // and provide that value at the end to pass it along the full chain of operations.
  //
  // Example:
  final case class Sample(a: Int)
  val readerSample: Reader[Sample, String] = Reader(s ⇒ s.a.toString)
  readerSample.run(Sample(0))
  // The advantage over plain functions (which are a Functor) is that we can chain Reader monads ensuring at compile time
  // they all depend on the same Input type, and chain operations accordingly.

  // State - State allows us to pass additi􏰀onal state around as part of a computa􏰀tion
  // Given State S and result A a State monad is equivalent to S => (S, A)
  // That is, given an input state, we can obtain a value and some output state
  //
  // Flatmapping over State monad represents a set of atomic operations that modify the state, which is a way to have safe
  // mutable values in your program. You can see Ref (RefInfo) for a similar structure in cats-effect

}
