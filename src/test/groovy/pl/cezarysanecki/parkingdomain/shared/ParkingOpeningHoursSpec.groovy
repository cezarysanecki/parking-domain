package pl.cezarysanecki.parkingdomain.shared

import pl.cezarysanecki.parkingdomain.commons.date.DateProvider
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

class ParkingOpeningHoursSpec extends Specification {

  static final LocalDate DAY = LocalDate.of(2020, 10, 10)

  def "at #time: can occupy = #canOccupy, remind about releasing = #remind, technical break = #technicalBreak"() {
    given:
      def instant = at(LocalTime.parse(time))

    expect:
      ParkingOpeningHours.canOccupyAt(instant) == canOccupy
      ParkingOpeningHours.isTimeToRemindAboutReleasing(instant) == remind
      ParkingOpeningHours.isTechnicalBreak(instant) == technicalBreak

    where:
      time    || canOccupy | remind | technicalBreak
      "04:59" || false     | false  | true
      "05:00" || true      | false  | false
      "12:00" || true      | false  | false
      "23:59" || true      | false  | false
      "00:00" || false     | true   | false
      "00:59" || false     | true   | false
      "01:00" || false     | false  | true
      "03:00" || false     | false  | true
  }

  private static Instant at(LocalTime time) {
    return ZonedDateTime.of(DAY, time, DateProvider.ZONE_OFFSET).toInstant()
  }

}
