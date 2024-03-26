package pl.cezarysanecki.parkingdomain.client.reservationrequest.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFactory
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.noReservationRequests
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId

class CancellingReservationRequestTest extends Specification {
  
  ClientId clientId = anyClientId()
  ReservationId reservationId = anyReservationId()
  
  LocalDateTime now = LocalDateTime.now()
  
  ClientReservationRequestsRepository repository = Stub()
  DateProvider dateProvider = Stub()
  ClientReservationRequestsFactory clientReservationRequestsFactory = new ClientReservationRequestsFactory(dateProvider)
  
  def 'should successfully cancel reservation request for parking spot if there is one'() {
    given:
      CancellingReservationRequest cancellingReservationRequest = new CancellingReservationRequest(repository)
    and:
      persisted(reservationRequestsWith(clientId, reservationId, now))
    
    when:
      def result = cancellingReservationRequest.cancelRequest(new CancelReservationRequestCommand(reservationId))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject cancelling reservation request for parking spot if there is any'() {
    given:
      CancellingReservationRequest cancellingReservationRequest = new CancellingReservationRequest(repository)
    and:
      persisted(noReservationRequests(clientId, now))
    
    when:
      def result = cancellingReservationRequest.cancelRequest(new CancelReservationRequestCommand(reservationId))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if client reservations do not exist'() {
    given:
      CancellingReservationRequest cancellingReservationRequest = new CancellingReservationRequest(repository)
    and:
      unknownClientReservationRequests()
    
    when:
      def result = cancellingReservationRequest.cancelRequest(new CancelReservationRequestCommand(reservationId))
    
    then:
      result.isFailure()
  }
  
  ClientReservationRequests persisted(ClientReservationRequests clientReservations) {
    repository.findBy(reservationId) >> clientReservations
    repository.publish(_ as ClientReservationRequestsEvent) >> clientReservations
    return clientReservations
  }
  
  void unknownClientReservationRequests() {
    repository.findBy(reservationId) >> Option.none()
  }
  
}
