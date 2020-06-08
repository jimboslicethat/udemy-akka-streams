package part1_recap

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, PoisonPill, Props, Stash, SupervisorStrategy}
import akka.util.Timeout

object AkkaRecap extends App {

  class SimpleActor extends Actor with ActorLogging with Stash {
    def receive: Receive = {
      case "createChild" =>
        val childActor = context.actorOf(Props[SimpleActor], "myChild")
        childActor ! "hello"
      case "stashThis" => stash()
      case "change handler NOW" =>
        unstashAll()
        context.become(anotherHandler)
      case "change" => context.become(anotherHandler)
      case message => println(s"I received $message")
    }

    def anotherHandler: Receive = {
      case message => println(s"In another recieve handler: $message")
    }

    override def preStart(): Unit = {
      log.info("Im starting!")
    }

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: RuntimeException => Restart
      case _ => Stop
    }
  }
  // actor encapsulation
  val system = ActorSystem("AkkaRecap")
  // #1 You may only instantiate an actor through the acto system.
  val actor = system.actorOf(Props[SimpleActor], "SimpleActor")
  // #2: The only way to communicate with an actor is by sending a message.
  actor ! "hello" //! is method for "tell actor message"
  /*
   - message are sent async
   - many actors (in the millions) can share a few dozen threads.
  - Each message is processed/handled ATOMICALLY. No race conditions.
  - No need for locks (for reason above).
  * */

  // changing actor behavior + stashing
  // actors can spawn other actors (parent + child relationship)
  // guardians: /system, /user, / (root guardian)

  // actors have a defined lifecycle. They can be started, stopped, suspended, resumed, restarted.

  // stopping actors - context.stop
  //  actor ! PoisonPill

  // logging
  // supervision

  // configure Akka infrastructure: dispatchers, routers, mailboxes, etc.

  // schedulers
  import scala.concurrent.duration._
  import system.dispatcher

  system.scheduler.scheduleOnce(2 seconds) {
    actor ! "delayed happy birthday!"
  }

  // akka patterns include FSM + ask pattern.

  import akka.pattern.ask
  implicit val timeout = Timeout(3 seconds)

  val future = actor ? "question"

  // use ask with pipe pattern
  import akka.pattern.pipe

  val anotherActor= system.actorOf(Props[SimpleActor], "anotherSimpleActor")
  future.mapTo[String].pipeTo(anotherActor) // when this future completes send it to another actor as a message.
}
