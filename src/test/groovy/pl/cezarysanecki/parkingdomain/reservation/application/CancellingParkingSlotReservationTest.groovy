package pl.cezarysanecki.parkingdomain.reservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservation
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationCancelled
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.individual
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.parkingSpotReservationsWith

class CancellingParkingSlotReservationTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationId reservationId = anyReservationId()
  
  ParkingSpotReservationsRepository repository = Stub()
  
  def 'should successfully cancel reservation'() {
    given:
      CancellingReservationEventListener cancellingReservationEventListener = new CancellingReservationEventListener(repository)
    
    when:
      cancellingReservationEventListener.handle(new ReservationRequestCancelled(clientId, reservationId))
    
    then:
      1 * repository.publish(new ReservationCancelled(parkingSpotId, reservationId))
  }
  
  def 'should reject cancelling reservation when it is not present'() {
    given:
      CancellingReservationEventListener cancellingReservationEventListener = new CancellingReservationEventListener(repository)
    and:
      def dayPartReservations = new ParkingSpotReservation(ReservationPeriod.evening(), individual(reservationId))
    and:
      persisted(reservationId, parkingSpotReservationsWith(parkingSpotId, dayPartReservations))
    
    when:
      cancellingReservationEventListener.handle(new ReservationRequestCancelled(clientId, reservationId))
    
    then:
      1 * repository.publish(new ReservationCancelled(parkingSpotId, reservationId))
  }
  
  ParkingSpotReservations persisted(ReservationId reservationId, ParkingSpotReservations parkingSpotReservations) {
    repository.findBy(reservationId) >> Option.of(parkingSpotReservations)
    repository.publish(_ as ParkingSpotReservationsEvent) >> parkingSpotReservations
    return parkingSpotReservations
  }
  
  void unknownParkingSpotReservations(ReservationId reservationId) {
    repository.findBy(reservationId) >> Option.none()
  }
  
}
