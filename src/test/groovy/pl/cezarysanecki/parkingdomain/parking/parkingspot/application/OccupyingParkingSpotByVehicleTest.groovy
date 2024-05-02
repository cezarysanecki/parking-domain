package pl.cezarysanecki.parkingdomain.parking.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupationFailed
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupiedEvents
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotFixture.emptyOpenParkingSpotWithCapacity

class OccupyingParkingSpotByVehicleTest extends Specification {
  
  ParkingSpots parkingSpots = Mock()
  
  @Subject
  VehicleParkedEventHandler vehicleParkedEventHandler = new VehicleParkedEventHandler(parkingSpots)
  
  def "should occupy parking spot by vehicle"() {
    given:
      def openParkingSpot = emptyOpenParkingSpotWithCapacity(4)
    and:
      parkingSpots.findBy(openParkingSpot.parkingSpotId) >> Option.of(openParkingSpot)
    
    when:
      vehicleParkedEventHandler.handle(new VehicleEvent.VehicleParked(
          VehicleId.newOne(), SpotUnits.of(2), openParkingSpot.parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == openParkingSpot.parkingSpotId
            && it.parkingSpotOccupied
            && it.fullyOccupied.isEmpty()
      } as OccupiedEvents)
  }
  
  def "should occupy parking spot by vehicle and fully occupy it"() {
    given:
      def openParkingSpot = emptyOpenParkingSpotWithCapacity(4)
    and:
      parkingSpots.findBy(openParkingSpot.parkingSpotId) >> Option.of(openParkingSpot)
    
    when:
      vehicleParkedEventHandler.handle(new VehicleEvent.VehicleParked(
          VehicleId.newOne(), SpotUnits.of(4), openParkingSpot.parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == openParkingSpot.parkingSpotId
            && it.parkingSpotOccupied
            && it.fullyOccupied.isDefined()
      } as OccupiedEvents)
  }
  
  def "fail to occupy parking spot by vehicle when parking spot does not have enough space"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def openParkingSpot = emptyOpenParkingSpotWithCapacity(2)
    and:
      parkingSpots.findBy(openParkingSpot.parkingSpotId) >> Option.of(openParkingSpot)
    
    when:
      vehicleParkedEventHandler.handle(new VehicleEvent.VehicleParked(
          vehicle, SpotUnits.of(4), openParkingSpot.parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == openParkingSpot.parkingSpotId
            && it.vehicleId == vehicle
      } as OccupationFailed)
  }
  
  def "fail to occupy parking spot by vehicle when parking spot does not exist"() {
    given:
      def vehicle = VehicleId.newOne()
    and:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpots.findBy(parkingSpotId) >> Option.none()
    
    when:
      vehicleParkedEventHandler.handle(new VehicleEvent.VehicleParked(
          vehicle, SpotUnits.of(4), parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == parkingSpotId
            && it.vehicleId == vehicle
      } as OccupationFailed)
  }
  
}
