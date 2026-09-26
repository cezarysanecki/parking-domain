package pl.cezarysanecki.parkingdomain.occupationreleasenotification

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class FindingOccupantsToRemindIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  OccupationReleaseNotificationRepository repository

  def "finds occupants of all current occupations"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      def occupantId = new OccupantId(UUID.randomUUID())
      def releasedOccupantId = new OccupantId(UUID.randomUUID())
      def released = OccupationId.newOne()
      repository.saveNew(parkingSpotId, ParkingSpotCapacity.defaultCapacity())
      repository.addOccupation(OccupationId.newOne(), occupantId, parkingSpotId, new SpotUnits(2))
      repository.addOccupation(released, releasedOccupantId, parkingSpotId, new SpotUnits(2))
      repository.removeOccupation(released)

    when:
      def result = repository.findAllOccupants()

    then:
      result.contains(new OccupantToRemindAboutClosing(occupantId, parkingSpotId))
      result.every { it.occupantId() != releasedOccupantId }
  }

}
