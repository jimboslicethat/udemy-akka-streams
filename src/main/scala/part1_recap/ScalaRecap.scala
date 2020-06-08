package part1_recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object ScalaRecap extends App { // extends App will get you 'run' IDe support in IntelliJ
  val aCondition: Boolean = false

  def myFunction(x: Int) = {
    if (x > 4) 42 else 65
  }
  // instructions vs expressions (instructions are imperative, expressions give values

  // OO features of Scala
  class Animal
  trait Carnivore {
    def eat(a:Animal) : Unit
  }
  object Carnivore

  //generics
  abstract class MyList[+A]
  // covariant [+A] = If your generic type contains or creates elements of type T, it should be +T.
  // contravariant [-A] = If your generic type acts on or consumes elements of type T, it should be contravariant.
  // invariant [A] = no variance _(same type passed generically)_. Useful when extra type support is needed.

  // method notations
  1 + 2 // infix notation (your implementing the method + like below)
  1.+(2) // same thing.

  // Monads: Option, Try

  // Pattern Matching!
  val unknown: Any = 2
  val order = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"
  }

  // try catches in scala are actually pattern matches.

  /*
  Scala Advanced Features
  * */

  // multithreading
  import scala.concurrent.ExecutionContext.Implicits.global

  val future = Future { // futures need an ExecutionContext before they can be used.
    42
  }
  // map, flatMap + other niceties
  future.onComplete {
    case Success(value) => println(s"I found the meaning of life: $value")
    case Failure(exception) => println(s"I found $exception while searching for the meaning of life!")
  } // this is completed on SOME other thread.

  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case _ => 999
  } // All partial functions are based on pattern matching.

  // type aliases (similar to TS)
  type AkkaReceive = PartialFunction[Any, Unit]

  def receive: AkkaReceive = {
    case 1 => println("hello!")
    case _ => println("confused...")
  }

  // Implicits!
  implicit val timeout = 3000

  def setTimeout(f:() => Unit)(implicit timeout: Int): Unit = f()

  setTimeout(() => println("timeout!")) // implicitly injected by the compiler by using "implicit" keyword

  // import scope
  // certain globals will need to be made available to the compiler when working with akka streams.
}
