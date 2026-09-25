package pl.cezarysanecki.parkingdomain.shared

import spock.lang.Specification

class SpotUnitsSpec extends Specification {

  def "#value is valid number of spot units"() {
    expect:
      new SpotUnits(value).value() == value

    where:
      value << [1, 2, 4, 8, 16, 1024]
  }

  def "#value is rejected because it is not positive"() {
    when:
      new SpotUnits(value)

    then:
      def exception = thrown(IllegalArgumentException)
      exception.message == "spot units cannot be negative"

    where:
      value << [0, -1, -4]
  }

  def "#value is rejected because it is not a power of two"() {
    when:
      new SpotUnits(value)

    then:
      def exception = thrown(IllegalArgumentException)
      exception.message == "spot units must be a power of two"

    where:
      value << [3, 5, 6, 7, 12]
  }

  def "spot units are printed as plain number"() {
    expect:
      new SpotUnits(4).toString() == "4"
  }

}
