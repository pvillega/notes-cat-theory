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

import cats._
import cats.data.Const
import cats.effect.IO
import cats.implicits._

object TaglessFinalInfo {

  // Various notes on Tagless Final
  // Tagless Final allows you to separate declaration of program vs execution via interpreter

  // Free vs Tagless
  // In Free we can inspect the internal structure and optimise things
  // Free is stack safe by default. Not all monads are.
  // Tagless has little to no boilerplate, thus more performant
  // Tagless doesn't require a Monad/Applicative, you could use a Functor or any other low-power abstraction

  // How to mitigate the advantages of Free.
  // Optimitzation: how to optimise without a peek-ahead structure
  // Solution: interpret program twice, first to optimise and then to run it (see Sphynx library)
  // Example:
  trait KvStore[F[_]] {
    def get(key: String): F[Option[String]]
    def put(key: String, value: String): F[Unit]
  }
  val interp: KvStore[IO] = ???
  // program first does puts then gets on the store
  def program[F[_]: Applicative](gets: List[String],
                                 puts: List[(String, String)])(F: KvStore[F]): F[List[String]] =
    puts.traverse(t ⇒ F.put(t._1, t._2)) *> gets.traverse(F.get).map(_.flatten)

  // we could try to run this in parallel, or remove duplicates, or if we do a put and get with same key we don't need the get, we know the answer
  // to optimise first we want to extract information of our program via a pre-interpreter
  // we can lift any 'Monoid' to an 'Applicative' via 'Const' type. Const type retains left param and discards right param
  case class KvStoreInfo(gets: Set[String], puts: Map[String, String])
  val extractor = new KvStore[Const[KvStoreInfo, ?]] {
    def get(key: String): Const[KvStoreInfo, Option[String]] =
      Const(KvStoreInfo(Set(key), Map.empty))

    def put(key: String, value: String): Const[KvStoreInfo, Unit] =
      Const(KvStoreInfo(Set.empty, Map(key → value)))
  }
  // we can already run this program to get our KvStoreInfo
  val gs                         = Nil
  val ps: List[(String, String)] = Nil
  val info: KvStoreInfo          = program(gs, ps)(extractor).getConst

  // We can now create a new interpreter that uses that output to precompute values and generate a new interpeter, optimised
  val optimisedInterp =
    info.gets
      .filterNot(info.puts.contains)
      .parTraverse(key ⇒ interp.get(key).map(_.map(s ⇒ (key, s))))
      .map { list: List[Option[(String, String)]] ⇒
        val table = list.flatten.toMap
        new KvStore[IO] {
          def get(key: String): IO[Option[String]] =
            table.get(key).orElse(info.puts.get(key)) match {
              case Some(a) ⇒ Option(a).pure[IO]
              case None    ⇒ interp.get(key) // should never happen but just in case
            }
          def put(key: String, value: String): IO[Unit] = interp.put(key, value)

        }

      }

  // first we exclude gets for values that are in the puts list
  // then via parTraverse we do gets for those keys (in parallel) we have to do a get using our original interpreter 'interp'
  // finally we create a new KvStore interpreter, over 'IO', which:
  //  - for get, checks the in memory table or the puts list for a value
  //  - for put we just use the original interpreter to store the values as we have to store them anyway
  // This can be generalised with 'sphynx' library which does part of the above process for you

  // Library 'mainecoon' allows us to automate the process of applying natural transformations to Tagless final interpreters,
  // which means we can convert one non-stack-safe interpreter to an stack-safe one. We can even map the F to a Free[F, ?]
  // and then run the Free with 'foldMap(FunctionK.id)' to run it in a stack safe mode.
  // It also provides a CartesianK helper that allows you to run multiple interpreters in parallel at once agains a single program
  // and retrieve all the results at once.
}
