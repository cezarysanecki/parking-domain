package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository
import pl.cezarysanecki.parkingdomain.parking.model.model.SpotUnits
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Reservation
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithCapacity
import static pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpotEvent.OccupationFailed
import static pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpotEvent.OccupiedEvents
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithReservation

class OccupyingParkingSpotByVehicleTest extends Specification {
  
  EventPublisher eventPublisher = Mock()
  BeneficiaryRepository beneficiaryRepository = Mock()
  ParkingSpotRepository parkingSpotRepository = Mock()
  
  @Subject
  OccupyingParkingSpot occupyingParkingSpot = new OccupyingParkingSpot(
      eventPublisher,
      beneficiaryRepository,
      parkingSpotRepository)
  
  def "should occupy parking spot by beneficiary"() {
    given:
      def emptyParkingSpot = emptyParkingSpotWithReservation(new Reservation())
    and:
      parkingSpotRepository.findBy(emptyParkingSpot.parkingSpotId) >> Option.of(emptyParkingSpot)
    
    when:
      occupyingParkingSpot.occupy()
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotId == emptyParkingSpot.parkingSpotId
            && it.parkingSpotOccupied
            && it.fullyOccupied.isFull()
      } as OccupiedEvents)
  }
  
  def "should occupy parking spot by vehicle and fully occupy it"() {
    given:
      def openParkingSpot = emptyParkingSpotWithCapacity(4)
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
      def openParkingSpot = emptyParkingSpotWithCapacity(2)
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
