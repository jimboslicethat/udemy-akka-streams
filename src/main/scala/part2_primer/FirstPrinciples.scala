package part2_primer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

object FirstPrinciples extends App {
  implicit val system = ActorSystem("FirstPrinciples")
  implicit val materializer = ActorMaterializer()

  // sources
  val source =  Source(1 to 10)
  // sinks
  val sink = Sink.foreach[Int](println)

  val graph = source.to(sink)
  //  graph.run()

  // flows transform elements
  val flow = Flow[Int].map(x => x + 1)

  val sourceWithFlow = source.via(flow) // sourceWithFlow returns a new source via composition.
  val flowWithSink = flow.to(sink) // flowWithSink returns a new sink via composition.

  // Each of these does the same thing:
//  sourceWithFlow.to(sink).run()
//  source.to(flowWithSink).run()
//  source.via(flow).to(sink).run()

  // various kinds of sources
  val finiteSource = Source.single(1)
  val anotherFiniteSource = Source(List(1,2,3))
  val emptySource = Source.empty[Int]
  val infiniteSource = Source(Stream.from(1)) // do not confuse an Akka stream with a "collection" Stream.

  import scala.concurrent.ExecutionContext.Implicits.global
  val futureSource = Source.fromFuture(Future(42))

  // sinks
  val theMostBoringSink = Sink.ignore
  val foreachSink = Sink.foreach[String](println)
  val headSink = Sink.head[Int] // retrieves the head and then closes the stream.
  val foldSink = Sink.fold[Int, Int](0)((a, b) => a + b)

  // flows - usually mapped to collection operators
  val mapFlow = Flow[Int].map(x => 2 * x)
  val takeFlow = Flow[Int].take(5)
  // drop, filter, etc...
  // NOT have flatMap. (This is where akka streams various greatly from say...Spark)

  // source -> flow -> flow -> ... -> sink
  val doubleFlowGraph = source.via(mapFlow).via(takeFlow).to(sink)
  //doubleFlowGraph.run()

  // syntactic sugars
  val mapSource = Source(1 to 10).map(x => x * 2) // .map on a source is the equivalent of - Source(1 to 10).via(Flow[Int].map(x => x * 2))
  // run streams directly
  //mapSource.runForeach(println) // equivalent of mapSource.to(Sink.foreach[Int](println)).run()

  // Akka Streams OPERATORS = components (what he calls them in the course).

  /*
  * Exercise: create a stream that takes the names of persons, then you will keep the first 2 names with length > 5 characters:
  * */
  val personSource = Source(List("James", "Eduardo", "Logan", "Leo", "Danaan", "Mackenzie", "Alex"))
  val filterNamesFlow = Flow[String].filter(name => name.length > 5)
  val limitPeopleFlow = Flow[String].take(2)
  val personSink = Sink.foreach[String](println)

  val personGraph = personSource.via(filterNamesFlow).via(limitPeopleFlow).to(personSink)
  personGraph.run()
}
