package org.hpi.esb.datavalidator.output.model

import org.scalatest.FunSpec

class ValidatorSeriesResultTest extends FunSpec {

  describe("ValidatorSeriesResultTest") {

    it("should merge") {
      val configValues = ConfigValues(
        sendingInterval = "100",
        sendingIntervalUnit = "SECONDS",
        scaleFactor = "1"
      )

      val resultValues1 = ResultValues(
        query = "Identity",
        correct = true,
        percentile = 1.3,
        rtFulfilled = true
      )
      val resultRow1 = ValidatorResultRow(configValues, resultValues1)

      val resultValues2 = ResultValues(
        query = "Identity",
        correct = false,
        percentile = 1.4,
        rtFulfilled = true
      )
      val resultRow2 = ValidatorResultRow(configValues, resultValues2)

      val expectedMergedResultValue = ResultValues(
        query = "Identity",
        correct = false,
        percentile = 1.35,
        rtFulfilled = true
      )
      val expectedResultRow = ValidatorResultRow(configValues, expectedMergedResultValue)

      val validatorSeriesResult = new ValidatorSeriesResult(List(resultRow1, resultRow2))

      assert(expectedResultRow.toTable() == validatorSeriesResult.toTable())

    }

  }
}
