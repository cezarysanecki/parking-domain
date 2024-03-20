package pl.cezarysanecki.parkingdomain.clientreservations.application

import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservations
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.noReservations
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.reservationsWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyReservationId

class RequestingReservationTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  LocalDateTime now = LocalDateTime.now()
  LocalDateTime properSinceReservation = now.plusDays(1)
  
  ClientReservationsRepository repository = Stub()
  
  def 'should successfully make reservation for random parking spot if there is no others'() {
    given:
      RequestingReservation requestingReservation = new RequestingReservation(repository)
    and:
      persisted(noReservations(clientId, now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestCommand(clientId, new ReservationSlot(properSinceReservation, 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject making reservation for random parking spot if there is too many of them'() {
    given:
      RequestingReservation requestingReservation = new RequestingReservation(repository)
    and:
      persisted(reservationsWith(clientId, anyReservationId(), now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestCommand(clientId, new ReservationSlot(now.plusHours(10), 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should successfully make reservation for chosen parking spot if there is no others'() {
    given:
      RequestingReservation requestingReservation = new RequestingReservation(repository)
    and:
      persisted(noReservations(clientId, now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestForChosenParkingSpotCommand(clientId, new ReservationSlot(properSinceReservation, 3), parkingSpotId))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject making reservation for chosen parking spot if there is too many of them'() {
    given:
      RequestingReservation requestingReservation = new RequestingReservation(repository)
    and:
      persisted(reservationsWith(clientId, anyReservationId(), now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestForChosenParkingSpotCommand(clientId, new ReservationSlot(properSinceReservation, 3), parkingSpotId))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should make reservation for any parking spot even if there is not stored client reservations'() {
    given:
      RequestingReservation requestingReservation = new RequestingReservation(repository)
    and:
      notPersisted(reservationsWith(clientId, anyReservationId(), now))
    
    when:
      def result = requestingReservation.createReservationRequest(
          new CreateReservationRequestCommand(clientId, new ReservationSlot(properSinceReservation, 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  ClientReservations persisted(ClientReservations clientReservations) {
    repository.findBy(clientReservations.clientId) >> clientReservations
    repository.publish(_ as ClientReservationsEvent) >> clientReservations
    return clientReservations
  }
  
  ClientReservations notPersisted(ClientReservations clientReservations) {
    repository.findBy(clientReservations.clientId) >> ClientReservations.empty(clientId, now)
    repository.publish(_ as ClientReservationsEvent) >> clientReservations
    return clientReservations
  }
  
}
