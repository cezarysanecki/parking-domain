package pl.cezarysanecki.parkingdomain.commons.date

import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateProviderTest extends Specification {
  
  LocalDateTime localDateTime
  
  DateProvider dateProvider = new DateProvider() {
    @Override
    LocalDate today() {
      return localDateTime.toLocalDate()
    }
    
    @Override
    LocalDateTime now() {
      return localDateTime
    }
  }
  
  def 'should return nearest future date being after chosen time'() {
    given:
      localDateTime = LocalDateTime.of(2020, 10, 10, 10, 10)
    
    when:
      def nearest = dateProvider.nearestFutureDateAt(LocalTime.of(4, 0))
    
    then:
      assert nearest == LocalDateTime.of(2020, 10, 11, 4, 0)
  }
  
  def 'should return nearest future date being before chosen time'() {
    given:
      localDateTime = LocalDateTime.of(2020, 10, 10, 2, 10)
    
    when:
      def nearest = dateProvider.nearestFutureDateAt(LocalTime.of(4, 0))
    
    then:
      assert nearest == LocalDateTime.of(2020, 10, 10, 4, 0)
  }
  
}
