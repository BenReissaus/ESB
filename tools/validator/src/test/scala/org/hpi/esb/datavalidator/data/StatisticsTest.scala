package org.hpi.esb.datavalidator.data

import org.scalatest.FunSuite

class StatisticsTest extends FunSuite {

  test("testConstructor") {
    val stats = new Statistics()
    assert(stats.min === Long.MaxValue)
    assert(stats.max === Long.MinValue)
    assert(stats.avg === 0)
    assert(stats.sum === 0)
    assert(stats.count === 0)
  }

  test("testGetUpdatedWithValue - update with single value") {
    val stats = new Statistics()
    val newValue = 10
    val newStats = stats.getUpdatedWithValue(newValue)

    assert(newStats.min == newValue)
    assert(newStats.max == newValue)
    assert(newStats.avg == newValue)
    assert(newStats.count == 1)
    assert(newStats.sum == newValue)
  }

  test("testGetUpdatedWithValue - update with multiple values") {
    val stats = new Statistics()
    val values = Seq(1,2,3,8,9)

    val newStats = values.foldLeft(stats)((s, value) => s.getUpdatedWithValue(value))

    assert(newStats.min === 1)
    assert(newStats.max === 9)
    assert(newStats.avg === 4.6)
    assert(newStats.sum === 23)
    assert(newStats.count === 5)
  }

  test("testPrettyPrint") {
    val stats = new Statistics(min = 2, max = 4, sum = 6, count = 2, avg = 3)
    assert(stats.prettyPrint == "Min: 2, Max: 4, Sum: 6, Count: 2, Avg: 3.0")
  }

  test("testToString") {
    val stats = new Statistics(min = 2, max = 4, sum = 6, count = 2, avg = 3)
    assert(stats.toString == "2,4,6,2,3.0")
  }

  test("testCreate - Successful") {
    val stats = Statistics.deserialize("2,4,6,2,3.0").get

    assert(stats.min === 2)
    assert(stats.max === 4)
    assert(stats.avg === 3.0)
    assert(stats.sum === 6)
    assert(stats.count === 2)
  }

  test("testCreate - Unsuccessful") {
    assertThrows[IllegalArgumentException] {
      Statistics.deserialize("incorrect string").get
    }
  }
}
