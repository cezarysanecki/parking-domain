package pl.cezarysanecki.parkingdomain.reservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservation
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.emptyParkingSpotReservations
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.individual
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.parkingSpotReservationsWith

class CreatingReservationRequestForParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  ParkingSpotReservationsRepository repository = Mock()
  
  def 'should successfully reserve part of any parking spot when it is empty'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def event = reservingPartOfParkingSpotRequestHasOccurred()
      def reservationId = event.reservationId
      def reservationPeriod = event.reservationPeriod
      def vehicleSizeUnit = event.vehicleSizeUnit
      def parkingSpotType = event.parkingSpotType
    and:
      persisted(emptyParkingSpotReservations(parkingSpotId), parkingSpotType, vehicleSizeUnit)
    
    when:
      makingParkingSlotReservation.handle(event)
    
    then:
      1 * repository.publish(new ReservationForPartOfParkingSpotMade(reservationId, reservationPeriod, parkingSpotId, vehicleSizeUnit))
  }
  
  def 'should fail to reserve part of any parking spot when there is no empty parking spot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def event = reservingPartOfParkingSpotRequestHasOccurred()
      def vehicleSizeUnit = event.vehicleSizeUnit
      def parkingSpotType = event.parkingSpotType
    and:
      unknownParkingSpotReservations(parkingSpotType, vehicleSizeUnit)
    
    when:
      makingParkingSlotReservation.handle(event)
    
    then:
      0 * repository.publish(_ as ReservationForPartOfParkingSpotMade)
  }
  
  def 'should reject reserving part of any parking spot when there is reservation on that slot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def event = reservingPartOfParkingSpotRequestHasOccurred()
      def vehicleSizeUnit = event.vehicleSizeUnit
      def parkingSpotType = event.parkingSpotType
    and:
      persisted(parkingSpotReservationsWith(parkingSpotId, new ParkingSpotReservation(individual(anyReservationId()), ReservationPeriod.morning())), parkingSpotType, vehicleSizeUnit)
    
    when:
      makingParkingSlotReservation.handle(event)
    
    then:
      1 * repository.publish(_ as ReservationFailed)
  }
  
  ParkingSpotReservations persisted(ParkingSpotReservations parkingSpotReservations, ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
    repository.findFor(parkingSpotType, vehicleSizeUnit) >> Option.of(parkingSpotReservations)
    return parkingSpotReservations
  }
  
  void unknownParkingSpotReservations(ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
    repository.findFor(parkingSpotType, vehicleSizeUnit) >> Option.none()
  }
  
  ReservingPartOfParkingSpotRequestHasOccurred reservingPartOfParkingSpotRequestHasOccurred() {
    ReservationId reservationId = ReservationId.of(UUID.randomUUID())
    return new ReservingPartOfParkingSpotRequestHasOccurred() {
      @Override
      ReservationId getReservationId() {
        return reservationId
      }
      
      @Override
      ReservationPeriod getReservationPeriod() {
        return ReservationPeriod.morning()
      }
      
      @Override
      VehicleSizeUnit getVehicleSizeUnit() {
        return VehicleSizeUnit.of(2)
      }
      
      @Override
      ParkingSpotType getParkingSpotType() {
        return ParkingSpotType.Gold
      }
    }
  }
  
}
