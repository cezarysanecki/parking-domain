package pl.cezarysanecki.parkingdomain.client.reservationrequest.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFactory
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.noReservationRequests
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId

class CreatingReservationRequestForAnyPartOfParkingSpotTest extends Specification {
  
  ClientId clientId = anyClientId()
  ReservationId reservationId = anyReservationId()
  
  LocalDateTime now = LocalDateTime.now()
  
  ClientReservationRequestsRepository repository = Stub()
  DateProvider dateProvider = Stub()
  ClientReservationRequestsFactory clientReservationRequestsFactory = new ClientReservationRequestsFactory(dateProvider)
  
  def 'should successfully create reservation request for any part of parking spot if there is no others'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository, clientReservationRequestsFactory)
    and:
      persisted(noReservationRequests(clientId, now))
    
    when:
      def result = requestingReservation.createRequest(
          new CreateReservationRequestForAnyPartOfParkingSpotCommand(clientId, ParkingSpotType.Gold, VehicleSizeUnit.of(2), ReservationPeriod.evening()))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject creation of reservation request for any part of parking spot if there is too many of them'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository, clientReservationRequestsFactory)
    and:
      persisted(reservationRequestsWith(clientId, reservationId, now))
    
    when:
      def result = requestingReservation.createRequest(
          new CreateReservationRequestForAnyPartOfParkingSpotCommand(clientId, ParkingSpotType.Gold, VehicleSizeUnit.of(2), ReservationPeriod.evening()))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should successfully create reservation for any part of parking spot even if there is no client reservation requests'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository, clientReservationRequestsFactory)
    and:
      notPersisted(noReservationRequests(clientId, now))
    
    when:
      def result = requestingReservation.createRequest(
          new CreateReservationRequestForAnyPartOfParkingSpotCommand(clientId, ParkingSpotType.Gold, VehicleSizeUnit.of(2), ReservationPeriod.evening()))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  ClientReservationRequests persisted(ClientReservationRequests clientReservations) {
    repository.findBy(clientReservations.clientId) >> clientReservations
    repository.publish(_ as ClientReservationRequestsEvent) >> clientReservations
    return clientReservations
  }
  
  ClientReservationRequests notPersisted(ClientReservationRequests clientReservations) {
    repository.findBy(clientReservations.clientId) >> Option.none()
    repository.publish(_ as ClientReservationRequestsEvent) >> clientReservations
    return clientReservations
  }
  
}
