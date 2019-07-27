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

package com.aracon.effects

import cats.effect._
import cats.effect.concurrent.Semaphore
import cats.implicits._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

// Provides a rate limiter for function calls based on a Semaphore from cats effect
// See https://medium.com/@olivier.melois/a-rate-limiter-in-15-lines-of-code-with-cats-effect-af09d838857a
object RateLimiting {

  /**
    * @tparam A the input
    * @tparam B the output
    * @param T         timer associated to the effect type, granting
    *                  the capability to sleep
    * @param CS        pretty much an ExecutionContext, required for fibers
    * @param semaphore : our tracking-construct: holds a number of
    *                  permits  distributes-them / gather them back. Instantiated with
    *                  a given number
    * @param function  : The function we want to limit calls to
    * @return a function of the same type
    */
  def rateLimited[A, B](
      semaphore: Semaphore[IO],
      function: A => IO[B]
  )(implicit T: Timer[IO], CS: ContextShift[IO]): A ⇒ IO[B] = { a: A =>
    for {
      // Asking for permission. If no more permit
      // is available, this computation will wait
      // asynchronously (ie no thread will be blocked)
      _ <- semaphore.acquire
      // Starting a timer counting 1 second in the
      // background (start == fork)
      timerFiber <- IO.sleep(1.second).start
      // Calling the function
      result <- function(a)
      // Waiting for the full second to elapse.
      // Because the timer is running in the
      // background, if the function's execution
      // takes more than one second, joining
      // will yield instantly. Otherwise, it'll
      // block the computation (asynchronously)
      // until the full second has passed.
      _ <- timerFiber.join
      // releasing the permit for the next function
      // call to pick up.
      _ <- semaphore.release
    } yield result
  }

}

object Usage {
  // function we want to limit
  def myFunction(a: Int): IO[String] = IO(a).map(_.toString)
  // big dataset
  val myData: List[Int] = List.fill(1000000)(1)

  implicit val cs: ContextShift[IO]             = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO]                 = IO.timer(ExecutionContext.global)
  implicit val concurrent: ConcurrentEffect[IO] = IO.ioConcurrentEffect(cs)

  val process: IO[List[String]] = for {
    semaphore <- Semaphore[IO](200)
    rateLimitedFunction = RateLimiting.rateLimited(semaphore, myFunction)
    // Applying our rateLimitedFunction to the full data set
    // parallelising as much as possible up to a limit of 200
    // calls per second.
    allResults <- myData.parTraverse(rateLimitedFunction)
  } yield allResults

  process.unsafeRunSync()
}
