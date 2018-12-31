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
import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.Nested
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object NestedInfo {
  // Nested is a data type that helps with composing `map` operations (functor composition)
  // For example, given the following structure in our code:

  val x: Option[Validated[String, Int]] = Some(Valid(123))

  // Instead of nesting calls like this:
  x.map(_.map(_.toString))

  // You can use Nested and do:
  val nested: Nested[Option, Validated[String, ?], Int] = Nested(Option(Validated.valid(123)))
  nested
    .map(_.toString)
    .value // res1: Option[cats.data.Validated[String,String]] = Some(Valid(123))

  // Nested work with Functor, Applicative, ApplicativeError, and Traversable types

  // Nested can also be sued to extend functions. For example, if we have:
  case class User(id: Int)
  case class UserInfo(id: Int)

  def createUser(userInfo: UserInfo): Future[Either[List[String], User]] = ???

  // and we want a function that creates multiple User at once, we can use Nesyted and Traverse to achieve it.
  // For example:
  def createUsers(userInfos: List[UserInfo]): Future[Either[List[String], List[User]]] =
    userInfos.traverse(userInfo => Nested(createUser(userInfo))).value

  // Wihtout Nested the traverse would act over the List[UserInfo] and return a List[Either[List[String], User]] instead of the type we want
}
