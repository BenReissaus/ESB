package org.hpi.esb.datavalidator.validation

import org.hpi.esb.datavalidator.metrics.{Correctness, ResponseTime}

object QueryValidationState {
  def getHeader: List[String] = List("Query", "Topic") ++ Correctness.header ++ ResponseTime.header
}

class QueryValidationState(val query: String, topicName: String,
                           val correctness: Correctness = new Correctness(),
                           val responseTime: ResponseTime = new ResponseTime()) {

  def updateCorrectness(isCorrect: Boolean): Unit = {
    correctness.update(isCorrect)
  }

  def updateResponseTime(value: Long): Unit = {
    responseTime.updateValue(value)
  }

  def fulfillsConstraints(): Boolean = correctness.fulfillsConstraint &&
  responseTime.fulfillsConstraint


  def getMeasuredResults: List[String] = List(query, topicName) ++ correctness.getMeasuredResults ++ responseTime.getMeasuredResults
}
