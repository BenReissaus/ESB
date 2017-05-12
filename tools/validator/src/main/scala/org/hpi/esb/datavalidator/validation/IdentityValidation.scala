package org.hpi.esb.datavalidator.validation

import akka.NotUsed
import akka.stream.scaladsl.GraphDSL
import akka.stream.{ActorMaterializer, Graph, SourceShape}
import org.hpi.esb.commons.util.Logging
import org.hpi.esb.datavalidator.config.Configurable
import org.hpi.esb.datavalidator.data.SimpleRecord
import org.hpi.esb.datavalidator.kafka.TopicHandler
import org.hpi.esb.datavalidator.validation.graphstage.ZipWhileEitherAvailable

class IdentityValidation(inTopicHandler: TopicHandler,
                         outTopicHandler: TopicHandler,
                         materializer: ActorMaterializer)
  extends Validation[SimpleRecord](inTopicHandler, outTopicHandler, materializer) with Configurable with Logging {

  override val valueName = "SimpleRecords"
  override val queryName = "Identity Query"

  def createSource(): Graph[SourceShape[(Option[SimpleRecord], Option[SimpleRecord])], NotUsed] = {

    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val zip = builder.add(ZipWhileEitherAvailable[SimpleRecord]())

      inTopicHandler.topicSource ~> take(inNumberOfMessages) ~> toSimpleRecords ~> zip.in0
      outTopicHandler.topicSource ~> take(outNumberOfMessages) ~> toSimpleRecords ~> zip.in1

      SourceShape(zip.out)
    }
  }
}
