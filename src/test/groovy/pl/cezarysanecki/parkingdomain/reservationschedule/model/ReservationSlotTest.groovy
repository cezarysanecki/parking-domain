package pl.cezarysanecki.parkingdomain.reservationschedule.model

import spock.lang.Specification

import java.time.LocalDateTime

class ReservationSlotTest extends Specification {
  
  def "cannot create slot which is longer then 12 hours"() {
    when:
      new ReservationSlot(LocalDateTime.now(), 13)
    
    then:
      thrown(IllegalArgumentException.class)
  }
  
  def "properly calculate until value"() {
    when:
      def reservationSlot = new ReservationSlot(LocalDateTime.of(2024, 10, 10, 10, 0), 12)
    
    then:
      reservationSlot.until() == LocalDateTime.of(2024, 10, 10, 22, 0)
  }
  
  def "reservation slots should intersect when #testcase"() {
    given:
      def reservationSlot = new ReservationSlot(firstSlotStart, hours)
    
    when:
      def result = reservationSlot.intersects(new ReservationSlot(secondSlotStart, hours))
    
    then:
      result
    
    where:
      testcase                            | firstSlotStart                        | secondSlotStart                       | hours
      "second starts before first ending" | LocalDateTime.of(2024, 10, 10, 10, 0) | LocalDateTime.of(2024, 10, 10, 15, 0) | 6
      "second ends after first start"     | LocalDateTime.of(2024, 10, 10, 15, 0) | LocalDateTime.of(2024, 10, 10, 10, 0) | 6
  }
  
  def "reservation slots should not intersect when #testcase"() {
    given:
      def reservationSlot = new ReservationSlot(firstSlotStart, hours)
    
    when:
      def result = reservationSlot.intersects(new ReservationSlot(secondSlotStart, hours))
    
    then:
      !result
    
    where:
      testcase                           | firstSlotStart                        | secondSlotStart                       | hours
      "second starts after first ending" | LocalDateTime.of(2024, 10, 10, 10, 0) | LocalDateTime.of(2024, 10, 10, 16, 0) | 6
      "second ends before first start"   | LocalDateTime.of(2024, 10, 10, 16, 0) | LocalDateTime.of(2024, 10, 10, 10, 0) | 6
  }
  
}
