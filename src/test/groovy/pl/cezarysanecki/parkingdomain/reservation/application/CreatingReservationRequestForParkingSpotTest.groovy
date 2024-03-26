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
  
  ReservationId reservationId = anyReservationId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  VehicleSizeUnit vehicleSizeUnit = VehicleSizeUnit.of(2)
  
  ParkingSpotReservationsRepository repository = Mock()
  
  def 'should successfully reserve part of any parking spot when it is empty'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      ReservationPeriod reservationPeriod = ReservationPeriod.morning()
    and:
      persisted(emptyParkingSpotReservations(parkingSpotId))
    
    when:
      makingParkingSlotReservation.handle(reservingPartOfParkingSpotRequestHasOccurred(ReservationPeriod.morning()))
    
    then:
      1 * repository.publish(new ReservationForPartOfParkingSpotMade(reservationId, reservationPeriod, parkingSpotId, vehicleSizeUnit))
  }
  
  def 'should successfully reserve part of any parking spot even it is not persisted'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      ReservationPeriod reservationPeriod = ReservationPeriod.morning()
    and:
      unknownParkingSpotReservations()
    
    when:
      makingParkingSlotReservation.handle(reservingPartOfParkingSpotRequestHasOccurred(ReservationPeriod.morning()))
    
    then:
      1 * repository.publish(new ReservationForPartOfParkingSpotMade(reservationId, reservationPeriod, parkingSpotId, vehicleSizeUnit))
  }
  
  def 'should reject reserving part of any parking spot when there is reservation on that slot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      persisted(parkingSpotReservationsWith(parkingSpotId, new ParkingSpotReservation(ReservationPeriod.morning(), individual(anyReservationId()))))
    
    when:
      makingParkingSlotReservation.handle(reservingPartOfParkingSpotRequestHasOccurred(ReservationPeriod.morning()))
    
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
  
  ReservingPartOfParkingSpotRequestHasOccurred reservingPartOfParkingSpotRequestHasOccurred(ReservationPeriod reservationPeriod) {
    return new ReservingPartOfParkingSpotRequestHasOccurred() {
      @Override
      ReservationId getReservationId() {
        return reservationId
      }
      
      @Override
      ReservationPeriod getReservationPeriod() {
        return reservationPeriod
      }
      
      @Override
      VehicleSizeUnit getVehicleSizeUnit() {
        return vehicleSizeUnit
      }
      
      @Override
      ParkingSpotType getParkingSpotType() {
        return ParkingSpotType.Gold
      }
    }
  }
  
}
