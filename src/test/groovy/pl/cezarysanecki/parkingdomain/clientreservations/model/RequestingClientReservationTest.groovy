package pl.cezarysanecki.parkingdomain.clientreservations.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestFailed
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.noReservations
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.reservationsWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId

class RequestingClientReservationTest extends Specification {
  
  private static final ClientId clientId = anyClientId()
  
  def "can make reservation for random parking spot"() {
    given:
      def clientReservations = noReservations(clientId)
      def reservationSlot = new ReservationSlot(LocalDateTime.now(), 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.requestReservation(reservationSlot)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientId
        assert it.reservationSlot == reservationSlot
        assert it.parkingSpotId.isEmpty()
      }
  }
  
  def "cannot make reservation for random parking spot when there is too many made reservations"() {
    given:
      def clientReservations = reservationsWith(clientId, 1)
      def reservationSlot = new ReservationSlot(LocalDateTime.now(), 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.requestReservation(reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "cannot have more reservations"
      }
  }
  
  def "can make reservation for chosen parking spot"() {
    given:
      def clientReservations = noReservations(clientId)
      def reservationSlot = new ReservationSlot(LocalDateTime.now(), 2)
      def parkingSpotId = anyParkingSpotId()
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.requestReservation(parkingSpotId, reservationSlot)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientId
        assert it.reservationSlot == reservationSlot
        assert it.parkingSpotId.get() == parkingSpotId
      }
  }
  
  def "cannot make reservation for chosen parking spot when there is too many made reservations"() {
    given:
      def clientReservations = reservationsWith(clientId, 1)
      def reservationSlot = new ReservationSlot(LocalDateTime.now(), 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.requestReservation(anyParkingSpotId(), reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "cannot have more reservations"
      }
  }
  
}
