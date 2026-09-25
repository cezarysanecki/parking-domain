package pl.cezarysanecki.parkingdomain.shared

import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TimeSlotSpec extends Specification {

  static final LocalDate DAY = LocalDate.of(2020, 10, 10)

  def "time slot is created for given day and hours in system time zone"() {
    when:
      def timeSlot = TimeSlot.create(DAY, 10, 15)

    then:
      timeSlot.from() == at(DAY, 10)
      timeSlot.to() == at(DAY, 15)
  }

  def "hour 24 as end of time slot means midnight of the next day"() {
    when:
      def timeSlot = TimeSlot.create(DAY, 18, 24)

    then:
      timeSlot.from() == at(DAY, 18)
      timeSlot.to() == at(DAY.plusDays(1), 0)
  }

  def "time slot cannot start after it ends"() {
    when:
      new TimeSlot(at(DAY, 15), at(DAY, 10))

    then:
      def exception = thrown(IllegalArgumentException)
      exception.message == "from cannot be after to"
  }

  def "time slot can be empty (from equals to)"() {
    when:
      def timeSlot = new TimeSlot(at(DAY, 10), at(DAY, 10))

    then:
      timeSlot.from() == timeSlot.to()
  }

  def "slot #from-#to is within 10-15: #expected"() {
    expect:
      TimeSlot.create(DAY, from, to).within(TimeSlot.create(DAY, 10, 15)) == expected

    where:
      from | to || expected
      10   | 15 || true
      11   | 14 || true
      10   | 12 || true
      9    | 12 || false
      12   | 16 || false
      16   | 17 || false
  }

  def "slot #from-#to intersects 10-15: #expected"() {
    expect:
      TimeSlot.create(DAY, from, to).intersects(TimeSlot.create(DAY, 10, 15)) == expected

    where:
      from | to || expected
      10   | 15 || true
      11   | 14 || true
      8    | 11 || true
      14   | 18 || true
      8    | 10 || true
      15   | 18 || true
      5    | 9  || false
      16   | 18 || false
  }

  private static Instant at(LocalDate day, int hour) {
    return ZonedDateTime.of(day, LocalTime.of(hour, 0), ZoneId.systemDefault()).toInstant()
  }

}
