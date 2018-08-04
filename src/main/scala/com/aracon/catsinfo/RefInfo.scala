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

import cats.implicits._
import cats.effect.{ Effect, IO }
import cats.effect.concurrent.Ref

object RefInfo {

  // Ref - An asynchronous, concurrent mutable reference. From cats-effect library
  // Provides safe concurrent access and modification of its content. Note that it always has an initial value,
  // it is an 'atomic reference'. For 'delayed values' ala Future see Deferred

  // Initialize
  val r: IO[Ref[IO, Int]] = Ref.of[IO, Int](0)
  // Note that, as usual with cats-effect constructs, Ref is wrapped in an Effect

  // The standard use case is to use it as an atomically modifiable element in some method, for example:
  def counter[F[_]](ref: Ref[F, Int])(implicit F: Effect[F]): F[Unit] =
    for {
      c1 <- ref.get
      _  <- F.delay(println(s">> $c1"))
      c2 <- ref.modify(x => (x + 1, x))
      _  <- F.delay(println(s">> $c2"))
    } yield ()
  // Note that as we pass the reference, this modification would be visible from other methods which receive the same reference.
  // That said, given 'F.delay' between 'get' and 'modify' concurrency may not be what you expect :)

  // An important reason to use Ref: replacing mutable collections in Scala
  // For example, imagine you have a system that works with multiple topics to which users can publish data. For all users to
  // access all topics some shared structure must be maintained that indexes the topics.
  //
  // A common solution is to use a 'mutable.Map' but that has some problems: mutable, non-synchronous, and not using F[_] which may
  // cause subtle bugs when working in a pure Effect-based stack of methods. Trying that you will end up with a lot of 'unsafeRun'
  // inside your codebase, where they shouldn't be. This solution is common, but not correct.
  //
  // The correct solution is to create a 'Ref[F, Map[K, Topic]]' when we start the server and share that Ref across the code.
  // This means we have an Effect-based component that can be mutated safely and can be accessed safely to read the topics of our
  // interest. This is a safer and correct solution. Thanks Ref!

}
