package pl.cezarysanecki.parkingdomain.commons.date

import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateProviderTest extends Specification {
  
  Instant instant
  
  DateProvider dateProvider = new DateProvider() {
    @Override
    Instant now() {
      return instant
    }
  }
  
  def 'should return nearest future date being after chosen time'() {
    given:
      instant = from(LocalDateTime.of(2020, 10, 10, 10, 10))
    
    when:
      def nearest = dateProvider.nearestFutureDateAt(4)
    
    then:
      assert nearest == from(LocalDateTime.of(2020, 10, 11, 4, 0))
  }
  
  def 'should return nearest future date being before chosen time'() {
    given:
      instant = from(LocalDateTime.of(2020, 10, 10, 2, 10))
    
    when:
      def nearest = dateProvider.nearestFutureDateAt(4)
    
    then:
      assert nearest == from(LocalDateTime.of(2020, 10, 10, 4, 0))
  }
  
  def 'should return tomorrow date at midnight'() {
    given:
      instant = from(LocalDateTime.of(2020, 10, 10, 2, 10))
    
    when:
      def tomorrowMidnight = dateProvider.tomorrowMidnight()
    
    then:
      assert tomorrowMidnight == from(LocalDateTime.of(2020, 10, 11, 0, 0))
  }
  
  Instant from(LocalDateTime localDateTime) {
    return Instant.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()))
  }
  
}
