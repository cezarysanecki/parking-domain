package pl.cezarysanecki.parkingdomain.client.reservationrequest.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.commands.ValidationError
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import spock.lang.Subject

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.noReservationRequests
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.reservationRequestsWith

class CancellingReservationRequestTest extends AbstractClientReservationRequestSpecification {
  
  ClientId clientId = anyClientId()
  ReservationId reservationId = anyReservationId()
  
  LocalDateTime now = LocalDateTime.now()
  
  @Subject
  CancellingReservationRequest sut = cancellingReservationRequest
  
  def setup() {
    dateProvider.setCurrentDate(now)
    clientReservationRequestCommandValidator.validate(_ as ClientReservationRequestCommand) >> Set.of()
  }
  
  def 'should successfully cancel reservation request for parking spot if there is one'() {
    given:
      persisted(reservationRequestsWith(clientId, reservationId))
    
    when:
      def result = sut.cancelRequest(new CancelReservationRequestCommand(reservationId, now))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def 'should reject cancelling reservation request for parking spot if there is any'() {
    given:
      persisted(noReservationRequests(clientId))
    
    when:
      def result = sut.cancelRequest(new CancelReservationRequestCommand(reservationId, now))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def 'should reject cancelling reservation request when command validation failed'() {
    given:
      LocalDateTime now = LocalDateTime.of(2020, 10, 10, 4, 30)
      dateProvider.setCurrentDate(now)
    and:
      persisted(reservationRequestsWith(clientId, reservationId))
    
    when:
      def result = sut.cancelRequest(new CancelReservationRequestCommand(reservationId, now))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
    and:
      clientReservationRequestCommandValidator.validate(_ as ClientReservationRequestCommand) >> Set.of(new ValidationError("any", "test msg"))
  }
  
  def 'should fail if client reservations do not exist'() {
    given:
      unknownClientReservationRequests()
    
    when:
      def result = sut.cancelRequest(new CancelReservationRequestCommand(reservationId, now))
    
    then:
      result.isFailure()
  }
  
  ClientReservationRequests persisted(ClientReservationRequests clientReservations) {
    repository.findBy(reservationId) >> Option.of(clientReservations)
    repository.publish(_ as ClientReservationRequestsEvent) >> clientReservations
    return clientReservations
  }
  
  void unknownClientReservationRequests() {
    repository.findBy(reservationId) >> Option.none()
  }
  
}
