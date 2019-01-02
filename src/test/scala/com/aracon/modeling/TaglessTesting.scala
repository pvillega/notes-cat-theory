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
import cats.effect._
import cats.implicits._
import cats.kernel.laws._
import cats.kernel.laws.discipline._
import org.scalacheck._
import org.scalacheck.Gen._
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import org.typelevel.discipline.scalatest.Discipline
import org.scalatest._

import scala.collection.mutable
import scala.util.Random

// Based on https://www.iteratorshq.com/blog/tagless-with-discipline-testing-scala-code-the-right-way/

// We want to avoid creating fixtures for every algebra, as that is tedious, repetitive, and error prone. Boilerplate
// The solution is to use Discipline to define laws about algebra, and test those laws. This means tests are more complete,
// less prone to breaking, and helps us think more about our algebra

// Example:

// Assume this model
final case class Email(address: String)
final case object EmailAlreadyExists

// and let's define Equals for Email as we will need it later on
object Email {
  implicit def eq: Eq[Email] = Eq.by(_.address)
}

// And this algebra
abstract class Emails[F[_]] {
  def save(email: Email): F[Either[EmailAlreadyExists.type, Email]]
  def known(email: Email): F[Boolean]
  def findEmail(email: Email): F[Option[Email]]
}

// And this sample implementation
final class EmailRepository extends Emails[IO] {
  private val emails = mutable.Map[String, Email]()

  override def save(email: Email): IO[Either[EmailAlreadyExists.type, Email]] =
    known(email).ifM(IO(Left(EmailAlreadyExists)), IO {
      emails.put(email.address, email); Right(email)
    })

  override def known(email: Email): IO[Boolean] = IO(emails.contains(email.address))

  override def findEmail(
      email: Email
  ): IO[Option[Email]] = IO(emails.get(email.address))
}

// We can define algebraic properties on the operations. Laws that any implementation must follow. This will guarantee
// correctness of implementation, and we can then verify other details with integration tests

// Example of laws for Emails:
// - For every saved email e, find(e) returns e ==> save(e) >> findEmail(e) <->  save(e) >> pure(Some(e))
// - For every saved email e, known(e) returns true ==> save(e) >> known(e) <->  save(e) >> pure(true)
// - Find is consistent with known i.e., find(e) is defined IFF known(e) is true ==> findEmail(e).fmap(_.isDefined) <-> known(e)
// - Saving the same email twice always returns EmailAlreadyExists error ==> save(e) *> save(e) <-> save(e) >> pure(Left(EmailAlreadyExists))

// Important note when defining laws:
// You need to be careful to also capture the effects in laws, not just the result. A good litmus test is to see if the law specifies a possible refactoring that doesn’t break anything.
// For example, I would not be able to blindly substitute (sides) by this law:
//    save(e) >> known(e) <-> pure(true)
// because it completely removes the effect of saving stuff. The correct law would be
//    save(e) >> known(e) <-> save(e) >> pure(true)

// Laws are implemented in Discipline as:
trait EmailAlgebraLaws[F[_]] {
  def algebra: Emails[F]
  implicit def M: Monad[F]

  def saveFindComposition(email: Email): IsEq[F[Option[Email]]] =
    algebra.save(email) >> algebra.findEmail(email) <-> (algebra.save(email) >> M.pure(Some(email)))

  def saveKnownComposition(email: Email): IsEq[F[Boolean]] =
    algebra.save(email) >> algebra.known(email) <-> (algebra.save(email) >> M.pure(true))

  def alreadyExistsCondition(email: Email): IsEq[F[Either[EmailAlreadyExists.type, Email]]] =
    algebra.save(email) *> algebra.save(email) <-> (algebra.save(email) *> M.pure(
      Left(EmailAlreadyExists)
    ))

  def findKnownConsistency(email: Email, f: Email => Email): IsEq[F[Boolean]] =
    (algebra.save(email) >> algebra
      .findEmail(f(email))
      .map(_.isDefined)) <-> (algebra
      .save(email) >> algebra.known(f(email)))
}

object EmailAlgebraLaws {

  def apply[F[_]](instance: Emails[F])(implicit ev: Monad[F]): EmailAlgebraLaws[F] =
    new EmailAlgebraLaws[F] {
      override val algebra: Emails[F]   = instance
      override implicit val M: Monad[F] = ev
    }

}

// And we define tests for implementations of our algebra as follows:
trait EmailAlgebraTests[F[_]] extends Laws {
  def laws: EmailAlgebraLaws[F]

  // The ruleset contains all the laws to test. New laws are added here, making it simple to extend tests in the future
  def algebra(implicit arbEmail: Arbitrary[Email],
              arbEmailF: Arbitrary[Email => Email],
              eqFBool: Eq[F[Boolean]],
              eqFOptId: Eq[F[Option[Email]]],
              eqFEither: Eq[F[Either[EmailAlreadyExists.type, Email]]]) =
    new SimpleRuleSet(
      name = "Emails",
      "find consistent with known" -> forAll(laws.findKnownConsistency _),
      "find and save compose"      -> forAll(laws.saveFindComposition _),
      "known and save compose"     -> forAll(laws.saveKnownComposition _),
      "ensure AlreadyExists"       -> forAll(laws.alreadyExistsCondition _)
    )
}

object EmailAlgebraTests {

  def apply[F[_]: Monad](instance: Emails[F]): EmailAlgebraTests[F] = new EmailAlgebraTests[F] {
    override val laws: EmailAlgebraLaws[F] = EmailAlgebraLaws(instance)
  }

}

// We then define how to generate Emails, standard boilerplate when using property-based testing:
trait ArbitraryInstances {
  final val MailGen: Gen[Email] = (for {
    mailbox  <- nonEmptyListOf(alphaNumChar).map(_.mkString)
    hostname <- nonEmptyListOf(alphaLowerChar).map(_.mkString)
  } yield s"$mailbox@$hostname.com") suchThat (_.length <= 254) map (Email(_))

  implicit final val ArbitraryEmail: Arbitrary[Email] = Arbitrary(MailGen)

  final val MailConsistency: Gen[Email ⇒ Email] = {
    // simple implementation
    def f(email: Email): Email = email.copy(address = s"xyz${email.address}")

    Gen.const(f)
  }

  implicit final val ArbitraryEmailConsistency: Arbitrary[Email => Email] = Arbitrary(
    MailConsistency
  )
}

// And finally we run the laws against our implementation:
class EmailRepositorySpecs extends FunSuite with ArbitraryInstances with Discipline {

  // sorry but we can only know if 2 IO are equal after running them!
  implicit def eqIO[A: Eq]: Eq[IO[A]] = (fx: IO[A], fy: IO[A]) ⇒ {
    (for {
      tx ← fx
      ty ← fy
    } yield implicitly[Eq[A]].eqv(tx, ty)).unsafeRunSync()
  }

  implicit def eqObj: Eq[EmailAlreadyExists.type] = Eq.fromUniversalEquals

  // we ask to check all the laws for the given implementation
  checkAll("EmailRepository", EmailAlgebraTests(new EmailRepository).algebra)

  // the following checks would fail (if uncommented) as the implementation violates the laws
  // checkAll("EmailRepositoryWithErrors", EmailAlgebraTests(new EmailRepositoryWithErrors).algebra)
}

final class EmailRepositoryWithErrors extends Emails[Id] {
  override def save(email: Email): Id[Either[EmailAlreadyExists.type, Email]] =
    if (Random.nextBoolean()) EmailAlreadyExists.asLeft else email.asRight

  override def known(email: Email): Id[Boolean] = Random.nextBoolean()

  override def findEmail(email: Email): Id[Option[Email]] =
    if (Random.nextBoolean()) Option(email) else None
}
