package pl.cezarysanecki.parkingdomain.client.reservationrequest.application


import pl.cezarysanecki.parkingdomain.commons.date.LocalDateProvider
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyReservationId

class ClientReservationRequestCommandValidatorTest extends Specification {
  
  ReservationId reservationId = anyReservationId()
  
  LocalDateProvider dateProvider = new LocalDateProvider()
  
  @Subject
  ClientReservationRequestCommandValidator sut = new ClientReservationRequestCommandValidator.Production(dateProvider)
  
  def "client should be able to change reservation request when #testcase"() {
    when:
      def errors = sut.validate(new CancelReservationRequestCommand(reservationId, now))
    
    then:
      assert errors.empty
    
    where:
      testcase                                   | now
      "request is made before 4 am previous day" | LocalDateTime.of(2020, 10, 10, 23, 59)
      "request is made before 4 am current day"  | LocalDateTime.of(2020, 10, 11, 3, 59)
      "request is made after 5 am current day"   | LocalDateTime.of(2020, 10, 10, 5, 0)
  }
  
  def "client should not be able to change reservation request when it is between 4 and 5 am"() {
    when:
      def errors = sut.validate(new CancelReservationRequestCommand(reservationId, now))
    
    then:
      assert errors.empty
    
    where:
      now << [
          LocalDateTime.of(2020, 10, 10, 4, 0),
          LocalDateTime.of(2020, 10, 10, 4, 30),
          LocalDateTime.of(2020, 10, 10, 4, 59)]
  }
  
}
