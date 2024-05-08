package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpotEvent.ReleasingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpotEvent.ReleasedEvents
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.fullyOccupiedBy
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.occupiedBy
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway

class ReleasingParkingSpotByVehicleTest extends Specification {
  
  ParkingSpots parkingSpots = Mock()
  
  VehicleDroveAwayEventHandler vehicleDroveAwayEventHandler = new VehicleDroveAwayEventHandler(parkingSpots)
  
  def "should parking spot be left by vehicle"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def occupiedParkingSpot = occupiedBy(VehicleId.newOne(), vehicle)
    and:
      parkingSpots.findBy(vehicle) >> Option.of(occupiedParkingSpot)
    
    when:
      vehicleDroveAwayEventHandler.handle(new VehicleDroveAway(vehicle))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
            && it.parkingSpotLeft
            && it.completelyFreedUp.isFull()
      } as ReleasedEvents)
  }
  
  def "should parking spot be left by vehicle and completely freed up if this is last vehicle"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def occupiedParkingSpot = fullyOccupiedBy(vehicle)
    and:
      parkingSpots.findBy(vehicle) >> Option.of(occupiedParkingSpot)
    
    when:
      vehicleDroveAwayEventHandler.handle(new VehicleDroveAway(vehicle))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
            && it.parkingSpotLeft
            && it.completelyFreedUp.isDefined()
      } as ReleasedEvents)
  }
  
  def "fail to leave parking spot by vehicle that is not parked on it"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def occupiedParkingSpot = fullyOccupiedBy(VehicleId.newOne())
    and:
      parkingSpots.findBy(vehicle) >> Option.of(occupiedParkingSpot)
    
    when:
      vehicleDroveAwayEventHandler.handle(new VehicleDroveAway(vehicle))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
            && it.vehicleId == vehicle
      } as ReleasingFailed)
  }
  
}
