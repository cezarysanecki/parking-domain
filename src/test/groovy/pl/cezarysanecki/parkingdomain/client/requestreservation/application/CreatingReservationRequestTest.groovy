package pl.cezarysanecki.parkingdomain.client.requestreservation.application


import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationsFixture.noReservationRequests
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId

class CreatingReservationRequestTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  LocalDateTime now = LocalDateTime.now()
  LocalDateTime properSinceReservation = now.plusDays(1)
  
  ClientReservationRequestsRepository repository = Stub()
  
  def 'should successfully make reservation for random parking spot if there is no others'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository)
    and:
      persisted(noReservationRequests(clientId, now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestForAnyParkingSpotCommand(clientId, new ReservationSlot(properSinceReservation, 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject making reservation for random parking spot if there is too many of them'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository)
    and:
      persisted(reservationRequestsWith(clientId, anyReservationId(), now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestForAnyParkingSpotCommand(clientId, new ReservationSlot(now.plusHours(10), 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should successfully make reservation for chosen parking spot if there is no others'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository)
    and:
      persisted(noReservationRequests(clientId, now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestForChosenParkingSpotCommand(clientId, new ReservationSlot(properSinceReservation, 3), parkingSpotId))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject making reservation for chosen parking spot if there is too many of them'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository)
    and:
      persisted(reservationRequestsWith(clientId, anyReservationId(), now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestForChosenParkingSpotCommand(clientId, new ReservationSlot(properSinceReservation, 3), parkingSpotId))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should make reservation for any parking spot even if there is not stored client reservations'() {
    given:
      CreatingReservationRequest requestingReservation = new CreatingReservationRequest(repository)
    and:
      notPersisted(reservationRequestsWith(clientId, anyReservationId(), now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestForAnyParkingSpotCommand(clientId, new ReservationSlot(properSinceReservation, 3)))
    
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
    repository.findBy(clientReservations.clientId) >> ClientReservationRequests.empty(clientId, now)
    repository.publish(_ as ClientReservationRequestsEvent) >> clientReservations
    return clientReservations
  }
  
}
