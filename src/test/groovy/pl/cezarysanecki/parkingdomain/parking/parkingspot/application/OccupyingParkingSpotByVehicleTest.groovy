package pl.cezarysanecki.parkingdomain.parking.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupationFailed
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupiedEvents
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
          VehicleId.newOne(), VehicleSize.of(2), openParkingSpot.parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == openParkingSpot.parkingSpotId
            && it.parkingSpotOccupied
            && it.fullyOccupied.isEmpty()
      } as ParkingSpotOccupiedEvents)
  }
  
  def "should occupy parking spot by vehicle and fully occupy it"() {
    given:
      def openParkingSpot = emptyOpenParkingSpotWithCapacity(4)
    and:
      parkingSpots.findBy(openParkingSpot.parkingSpotId) >> Option.of(openParkingSpot)
    
    when:
      vehicleParkedEventHandler.handle(new VehicleEvent.VehicleParked(
          VehicleId.newOne(), VehicleSize.of(4), openParkingSpot.parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == openParkingSpot.parkingSpotId
            && it.parkingSpotOccupied
            && it.fullyOccupied.isDefined()
      } as ParkingSpotOccupiedEvents)
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
          vehicle, VehicleSize.of(4), openParkingSpot.parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == openParkingSpot.parkingSpotId
            && it.vehicleId == vehicle
      } as ParkingSpotOccupationFailed)
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
          vehicle, VehicleSize.of(4), parkingSpotId))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == parkingSpotId
            && it.vehicleId == vehicle
      } as ParkingSpotOccupationFailed)
  }
  
}
