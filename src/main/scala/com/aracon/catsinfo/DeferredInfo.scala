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
import cats.effect._
import cats.effect.concurrent._

object DeferredInfo {
  // Deferred - A purely functional synchronization primitive which represents a single value which may not yet be available.
  // When created, a Deferred is empty. It can then be completed exactly once, and never be made empty again.

  // Deferred has a 'get' method that will BLOCK (WARNING - WILL BLOCK) until Deferred is completed and a value is available
  // Deferred has a method 'complete' that can be called once, subsequent calls will result in a failed Effect

  // A use case for Deferred is as a replacement of Future/Promise. You can pass it to a method that will operate in parallel and
  // request the value when you need it. It will automatically block without having to use 'Await' and similar, unlike Future.

  // Another use case is related to 'racing operations' in which we only care about the first result obtained, and we can discard the rest.
  // This way we ensure only the first value obtained is used, avoiding any possible concurrency issues.

  // Example from official documentation:
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val par: Parallel[IO, IO] = Parallel[IO, IO.Par].asInstanceOf[Parallel[IO, IO]]

  def start(d: Deferred[IO, Int]): IO[Unit] = {
    val attemptCompletion: Int => IO[Unit] = n => d.complete(n).attempt.void

    List(
      IO.race(attemptCompletion(1), attemptCompletion(2)),
      d.get.flatMap { n =>
        IO(println(s"Result: $n"))
      }
    ).parSequence.void
  }

  val program: IO[Unit] =
    for {
      d <- Deferred[IO, Int]
      _ <- start(d)
    } yield ()

}
