package org.hpi.esb.datavalidator.validation.graphstage

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.FunSuite

import scala.collection.immutable

class IgnoreLastElementTest extends FunSuite {

  implicit val system = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)

  test("happy list") {

    val start = 0
    val end = 10
    val values = immutable.Seq.range(start, end)

    val s = TestSink.probe[Int]

    val (_, sink) = Source(values)
      .via(new IgnoreLastElement[Int]())
      .toMat(s)(Keep.both)
      .run()

    val numberOfElements = end
    sink.request(numberOfElements)
    values.dropRight(1).foreach(v => sink.expectNext(v))
    sink.expectComplete()
  }

  test("empty list") {

    val s = TestSink.probe[Int]

    val (_, sink) = Source(List[Int]())
      .via(new IgnoreLastElement[Int]())
      .toMat(s)(Keep.both)
      .run()

    val numberOfElements = 1
    sink.request(numberOfElements)
    sink.expectComplete()
  }
}
