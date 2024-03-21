package pl.cezarysanecki.parkingdomain.reservation.schedule.application

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.schedule.application.ParkingSpotReservationEventListener
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSchedules
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

class HandlingOccupationForParkingSpotReservationTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  ReservationSchedules repository = Mock()
  
  def 'should successfully create parking spot reservation schedule'() {
    given:
      ParkingSpotReservationEventListener parkingSpotReservationEventListener = new ParkingSpotReservationEventListener(repository)
    
    when:
      parkingSpotReservationEventListener.handle(new ParkingSpotCreated(parkingSpotId, 4))
    
    then:
      1 * repository.createFor(parkingSpotId)
  }
  
  def 'should successfully parking spot reservation schedule as occupied'() {
    given:
      ParkingSpotReservationEventListener parkingSpotReservationEventListener = new ParkingSpotReservationEventListener(repository)
    
    when:
      parkingSpotReservationEventListener.handle(new VehicleParked(parkingSpotId, vehicleWith(2)))
    
    then:
      1 * repository.markOccupation(parkingSpotId, true)
  }
  
  def 'should successfully parking spot reservation schedule as free'() {
    given:
      ParkingSpotReservationEventListener parkingSpotReservationEventListener = new ParkingSpotReservationEventListener(repository)
    
    when:
      parkingSpotReservationEventListener.handle(new CompletelyFreedUp(parkingSpotId))
    
    then:
      1 * repository.markOccupation(parkingSpotId, false)
  }
  
}
