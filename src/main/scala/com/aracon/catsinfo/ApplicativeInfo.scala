package com.aracon.catsinfo

import cats._
import cats.implicits._

object ApplicativeInfo {

  // We can use Applicative to lift functions to be applied in an Applicative context, similar to what we do with Functors
  // For example:
  def lenght(s: String): Int = s.length
  val myVal = Option("value")
  // we can't apply 'length' directly to 'myVal' as types don't match. But we can lift the function as follows:
  val newLenght: Option[String] ⇒ Option[Int] = Applicative[Option].lift(lenght)
  newLenght(myVal)
  // This is the same example we saw with Functor, it works because Applicative extends Functor.
  // But with Applicative we can do the same for functions with multiple parameters:
  def twoParams(a: String, b: String): Int = a.length + b.length
  val newTwoParams: (Option[String], Option[String]) ⇒ Option[Int] = Applicative[Option].ap2(Option(twoParams))
  newTwoParams(myVal, myVal)
  // The difference between 'lift' and 'ap' is that 'ap' requires us to wrap the function in F (Option in this example)
  // There are version of 'ap' for one, two, three,... parameters:
  def threeParams(a: String, b: String, c: String): Int = a.length + b.length + c.length
  val newThreeParams: (Option[String], Option[String], Option[String]) ⇒ Option[Int] = Applicative[Option].ap3(Option(threeParams))
  newThreeParams(myVal, myVal, myVal)

  // If you don't want to wrap your functions on F you can use 'map2', 'map3', or 'mapN' for when you don't want to count params.
  Applicative[Option].map2(myVal, myVal)(twoParams)
  Applicative[Option].map3(myVal, myVal, myVal)(threeParams)
  (myVal, myVal).mapN(twoParams)
  (myVal, myVal, myVal).mapN(threeParams)
  // The difference with 'ap' is that you can't curry these methods so you can't reuse the 'lifting'
  // IMPORTANT NOTE: 'map2', ..., 'mapN' ignore sequencing. That is, if each param is an Effect, we can run them in parallel and group results at the end.

}
