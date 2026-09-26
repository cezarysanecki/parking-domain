package pl.cezarysanecki.parkingdomain.occupationreleasenotification

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.notification.NotificationFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification

class NotifyingAboutTowedVehicleSpec extends Specification {

  NotificationFacade notificationFacade = Mock()
  def repository = new InMemoryOccupationReleaseNotificationRepository()
  def facade = new OccupationReleaseNotificationFacade(repository, notificationFacade)
  def eventHandler = new OccupationReleaseNotificationEventHandler(repository, facade)

  def "occupant is notified that vehicle was towed"() {
    given:
      def occupantId = new OccupantId(UUID.randomUUID())
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())

    when:
      eventHandler.handle(new ParkingSpotForceReleased(
          OccupationId.newOne(), occupantId, parkingSpotId, new SpotUnits(4), ParkingSpotForceReleased.Reason.VEHICLE_TOWED))

    then:
      1 * notificationFacade.notify(new ClientId(occupantId.value()), { it.contains("towed") && it.contains(parkingSpotId.toString()) })
      0 * notificationFacade._
  }

  def "occupant is not notified about towing when parking spot is released by force for other reason"() {
    when:
      eventHandler.handle(new ParkingSpotForceReleased(
          OccupationId.newOne(), new OccupantId(UUID.randomUUID()), new ParkingSpotId(UUID.randomUUID()), new SpotUnits(4),
          ParkingSpotForceReleased.Reason.NOT_RELEASED_PARKING_SPOT))

    then:
      0 * notificationFacade._
  }

}
