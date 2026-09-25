package pl.cezarysanecki.parkingdomain.parking

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.commons.aggregates.AggregateRootIsStale
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class OptimisticLockingForOccupationIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  ParkingRepository parkingRepository
  @Autowired
  OccupantRepository occupantRepository
  @Autowired
  OccupationRepository occupationRepository

  def "optimistic locking rejects second occupation saved with stale parking spot version"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      def firstClientId = new ClientId(UUID.randomUUID())
      def secondClientId = new ClientId(UUID.randomUUID())
    and:
      parkingRepository.saveNew(ParkingSpot.create(parkingSpotId, ParkingSpotCapacity.defaultCapacity()))
      occupantRepository.saveNew(Occupant.newOne(firstClientId))
      occupantRepository.saveNew(Occupant.newOne(secondClientId))

    when:
      def parkingSpot = parkingRepository.loadBy(parkingSpotId)
      def firstOccupant = occupantRepository.findBy(new OccupantId(firstClientId.value()))
      def secondOccupant = occupantRepository.findBy(new OccupantId(secondClientId.value()))
    and:
      occupationRepository.saveCheckingVersion(
          new Occupation(new OccupationId(UUID.randomUUID()), firstOccupant, parkingSpot, new SpotUnits(2)))

    then:
      noExceptionThrown()

    when:
      occupationRepository.saveCheckingVersion(
          new Occupation(new OccupationId(UUID.randomUUID()), secondOccupant, parkingSpot, new SpotUnits(2)))

    then:
      thrown(AggregateRootIsStale)
  }

}
