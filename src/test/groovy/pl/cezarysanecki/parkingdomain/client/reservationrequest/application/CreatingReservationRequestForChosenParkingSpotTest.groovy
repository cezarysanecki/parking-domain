package pl.cezarysanecki.parkingdomain.client.reservationrequest.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.commands.ValidationError
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import spock.lang.Subject

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.noReservationRequests
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId

class CreatingReservationRequestForChosenParkingSpotTest extends AbstractClientReservationRequestSpecification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationId reservationId = anyReservationId()
  
  LocalDateTime now = LocalDateTime.now()
  
  @Subject
  CreatingReservationRequest sut = creatingReservationRequest
  
  def setup() {
    dateProvider.setCurrentDate(now)
    clientReservationRequestCommandValidator.validate(_ as ClientReservationRequestCommand) >> Set.of()
  }
  
  def 'should successfully create reservation request for chosen parking spot if there is no others'() {
    given:
      persisted(noReservationRequests(clientId))
    
    when:
      def result = sut.createRequest(new CreateReservationRequestForChosenParkingSpotCommand(
          clientId, parkingSpotId, ReservationPeriod.wholeDay(), now))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def 'should reject creation of reservation request for chosen parking spot if there is too many of them'() {
    given:
      persisted(reservationRequestsWith(clientId, reservationId))
    
    when:
      def result = sut.createRequest(new CreateReservationRequestForChosenParkingSpotCommand(
          clientId, parkingSpotId, ReservationPeriod.wholeDay(), now))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def 'should reject creation of reservation request when command validation failed'() {
    given:
      persisted(noReservationRequests(clientId))
    
    when:
      def result = sut.createRequest(new CreateReservationRequestForChosenParkingSpotCommand(
          clientId, parkingSpotId, ReservationPeriod.wholeDay(), now))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
    and:
      clientReservationRequestCommandValidator.validate(_ as ClientReservationRequestCommand) >> Set.of(new ValidationError("any", "test msg"))
  }
  
  def 'should successfully create reservation for chosen parking spot even if there is no client reservation requests'() {
    given:
      unknownClientReservationRequests(noReservationRequests(clientId))
    
    when:
      def result = sut.createRequest(new CreateReservationRequestForChosenParkingSpotCommand(
          clientId, parkingSpotId, ReservationPeriod.wholeDay(), now))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  ClientReservationRequests persisted(ClientReservationRequests clientReservations) {
    repository.findBy(clientReservations.clientId) >> Option.of(clientReservations)
    repository.publish(_ as ClientReservationRequestsEvent) >> clientReservations
    return clientReservations
  }
  
  ClientReservationRequests unknownClientReservationRequests(ClientReservationRequests clientReservations) {
    repository.findBy(clientReservations.clientId) >> Option.none()
    repository.publish(_ as ClientReservationRequestsEvent) >> clientReservations
    return clientReservations
  }
  
}
