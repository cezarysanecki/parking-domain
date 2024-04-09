package pl.cezarysanecki.parkingdomain.parking.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotLeavingOutFailed
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotLeftEvents
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotFixture.fullyOccupiedBy
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotFixture.occupiedBy
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway

class DrivingVehicleAwayHandledTest extends Specification {
  
  ParkingSpots parkingSpots = Mock()
  
  VehicleDroveAwayEventHandler vehicleDroveAwayEventHandler = new VehicleDroveAwayEventHandler(parkingSpots)
  
  def "should parking spot be left by vehicle"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def occupiedParkingSpot = occupiedBy(VehicleId.newOne(), vehicle)
    and:
      parkingSpots.findOccupiedBy(vehicle) >> Option.of(occupiedParkingSpot)
    
    when:
      vehicleDroveAwayEventHandler.handle(new VehicleDroveAway(vehicle))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
            && it.parkingSpotLeft
            && it.completelyFreedUp.isEmpty()
      } as ParkingSpotLeftEvents)
  }
  
  def "should parking spot be left by vehicle and completely freed up if this is last vehicle"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def occupiedParkingSpot = fullyOccupiedBy(vehicle)
    and:
      parkingSpots.findOccupiedBy(vehicle) >> Option.of(occupiedParkingSpot)
    
    when:
      vehicleDroveAwayEventHandler.handle(new VehicleDroveAway(vehicle))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
            && it.parkingSpotLeft
            && it.completelyFreedUp.isDefined()
      } as ParkingSpotLeftEvents)
  }
  
  def "fail to leave parking spot by vehicle that is not parked on it"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def occupiedParkingSpot = fullyOccupiedBy(VehicleId.newOne())
    and:
      parkingSpots.findOccupiedBy(vehicle) >> Option.of(occupiedParkingSpot)
    
    when:
      vehicleDroveAwayEventHandler.handle(new VehicleDroveAway(vehicle))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
            && it.vehicleId == vehicle
      } as ParkingSpotLeavingOutFailed)
  }
  
}
