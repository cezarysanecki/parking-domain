package pl.cezarysanecki.parkingdomain.occupationreleasenotification

import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.notification.NotificationFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification

class RemindingAboutClosingSpec extends Specification {

  NotificationFacade notificationFacade = Mock()
  def repository = new InMemoryOccupationReleaseNotificationRepository()
  def facade = new OccupationReleaseNotificationFacade(repository, notificationFacade)

  def setup() {
    InMemoryRepositories.clearAll()
  }

  def cleanup() {
    InMemoryRepositories.clearAll()
  }

  def "every current occupant is reminded to release parking spot before closing"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      def first = new OccupantId(UUID.randomUUID())
      def second = new OccupantId(UUID.randomUUID())
      def released = OccupationId.newOne()
      repository.saveNew(parkingSpotId, ParkingSpotCapacity.defaultCapacity())
      repository.addOccupation(OccupationId.newOne(), first, parkingSpotId, new SpotUnits(1))
      repository.addOccupation(OccupationId.newOne(), second, parkingSpotId, new SpotUnits(1))
      repository.addOccupation(released, new OccupantId(UUID.randomUUID()), parkingSpotId, new SpotUnits(1))
      repository.removeOccupation(released)

    when:
      def result = facade.remindAboutClosing()

    then:
      1 * notificationFacade.notify(new ClientId(first.value()), { it.contains(parkingSpotId.toString()) && it.contains("towed") })
      1 * notificationFacade.notify(new ClientId(second.value()), { it.contains(parkingSpotId.toString()) && it.contains("towed") })
      0 * notificationFacade._
      result == 2
  }

}
