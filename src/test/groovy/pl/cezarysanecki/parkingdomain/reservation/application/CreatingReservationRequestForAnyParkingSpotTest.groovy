package pl.cezarysanecki.parkingdomain.reservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservation
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationFailed
import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationForWholeParkingSpotMade
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.emptyParkingSpotReservations
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.individual
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.parkingSpotReservationsWith

class CreatingReservationRequestForAnyParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  ParkingSpotReservationsRepository repository = Mock()
  
  def 'should successfully reserve whole parking spot when it is empty'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def event = reservingWholeParkingSpotRequestHasOccurred(parkingSpotId, ReservationPeriod.morning())
      def reservationId = event.reservationId
      def reservationPeriod = event.reservationPeriod
    and:
      persisted(emptyParkingSpotReservations(parkingSpotId))
    
    
    when:
      makingParkingSlotReservation.handle(event)
    
    then:
      1 * repository.publish(new ReservationForWholeParkingSpotMade(reservationId, reservationPeriod, parkingSpotId))
  }
  
  def 'should successfully reserve whole parking spot even it is not persisted'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def event = reservingWholeParkingSpotRequestHasOccurred(parkingSpotId, ReservationPeriod.morning())
      def reservationId = event.reservationId
      def reservationPeriod = event.reservationPeriod
    and:
      unknownParkingSpotReservations()
    
    when:
      makingParkingSlotReservation.handle(event)
    
    then:
      1 * repository.publish(new ReservationForWholeParkingSpotMade(reservationId, reservationPeriod, parkingSpotId))
  }
  
  def 'should reject reserving whole parking spot when there is reservation on that slot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      persisted(parkingSpotReservationsWith(parkingSpotId, new ParkingSpotReservation(individual(anyReservationId()), ReservationPeriod.morning())))
    
    when:
      makingParkingSlotReservation.handle(reservingWholeParkingSpotRequestHasOccurred(parkingSpotId, ReservationPeriod.morning()))
    
    then:
      1 * repository.publish(_ as ReservationFailed)
  }
  
  ParkingSpotReservations persisted(ParkingSpotReservations parkingSpotReservations) {
    repository.findBy(parkingSpotId) >> Option.of(parkingSpotReservations)
    return parkingSpotReservations
  }
  
  void unknownParkingSpotReservations() {
    repository.findBy(parkingSpotId) >> Option.none()
  }
  
  ReservingWholeParkingSpotRequestHasOccurred reservingWholeParkingSpotRequestHasOccurred(ParkingSpotId parkingSpotId, ReservationPeriod reservationPeriod) {
    ReservationId reservationId = ReservationId.of(UUID.randomUUID())
    return new ReservingWholeParkingSpotRequestHasOccurred() {
      @Override
      ReservationId getReservationId() {
        return reservationId
      }
      
      @Override
      ReservationPeriod getReservationPeriod() {
        return reservationPeriod
      }
      
      @Override
      ParkingSpotId getParkingSpotId() {
        return parkingSpotId
      }
    }
  }
  
}
