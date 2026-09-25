package pl.cezarysanecki.parkingdomain.parking

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.shared.VehicleType

import static pl.cezarysanecki.parkingdomain.shared.VehicleType.MOTORCYCLE
import static pl.cezarysanecki.parkingdomain.shared.VehicleType.SCOOTER

/**
 * Works on the repositories and the aggregate, not on ParkingFacade, so it does not depend on
 * the wall clock of the integration context.
 */
class SharingParkingSpotByVehicleTypesIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  ParkingRepository parkingRepository
  @Autowired
  OccupantRepository occupantRepository
  @Autowired
  OccupationRepository occupationRepository

  def "parking spot stored in Postgres can be shared by motorcycle and two scooters and then it is full"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      parkingRepository.saveNew(ParkingSpot.create(parkingSpotId, ParkingSpotCapacity.defaultCapacity()))

    when:
      def results = [MOTORCYCLE, SCOOTER, SCOOTER].collect { occupy(parkingSpotId, it) }

    then:
      results == [true, true, true]

    when:
      def oneMoreScooter = occupy(parkingSpotId, SCOOTER)

    then:
      oneMoreScooter == false
  }

  private boolean occupy(ParkingSpotId parkingSpotId, VehicleType vehicleType) {
    def clientId = new ClientId(UUID.randomUUID())
    occupantRepository.saveNew(Occupant.newOne(clientId))
    def occupant = occupantRepository.findBy(new OccupantId(clientId.value()))
    def parkingSpot = parkingRepository.loadBy(parkingSpotId)
    if (!parkingSpot.occupyBy(vehicleType.spotUnits())) {
      return false
    }
    occupationRepository.saveCheckingVersion(
        new Occupation(new OccupationId(UUID.randomUUID()), occupant, parkingSpot, vehicleType.spotUnits()))
    return true
  }

}
