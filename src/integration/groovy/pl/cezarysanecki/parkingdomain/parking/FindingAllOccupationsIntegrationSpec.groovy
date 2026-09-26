package pl.cezarysanecki.parkingdomain.parking

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class FindingAllOccupationsIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  ParkingRepository parkingRepository
  @Autowired
  OccupantRepository occupantRepository
  @Autowired
  OccupationRepository occupationRepository

  def "finds all current occupations and skips released ones"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      def clientId = new ClientId(UUID.randomUUID())
      parkingRepository.saveNew(ParkingSpot.create(parkingSpotId, ParkingSpotCapacity.defaultCapacity()))
      occupantRepository.saveNew(Occupant.newOne(clientId))
    and:
      def current = OccupationId.newOne()
      occupationRepository.saveCheckingVersion(new Occupation(
          current, occupantRepository.findBy(new OccupantId(clientId.value())), parkingRepository.loadBy(parkingSpotId), new SpotUnits(2)))
      def released = OccupationId.newOne()
      occupationRepository.saveCheckingVersion(new Occupation(
          released, occupantRepository.findBy(new OccupantId(clientId.value())), parkingRepository.loadBy(parkingSpotId), new SpotUnits(2)))
      occupationRepository.delete(released)

    when:
      def result = occupationRepository.findAll()

    then:
      result.contains(current)
      !result.contains(released)
  }

}
