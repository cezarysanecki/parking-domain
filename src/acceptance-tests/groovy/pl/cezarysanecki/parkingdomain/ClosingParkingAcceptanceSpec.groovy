package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase.RemindingAboutParkingClosingUseCase
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.usecase.TowingVehiclesAfterClosingUseCase
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class ClosingParkingAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  RemindingAboutParkingClosingUseCase remindingAboutParkingClosingUseCase
  @Autowired
  TowingVehiclesAfterClosingUseCase towingVehiclesAfterClosingUseCase

  def "occupants still on parking at #hour:#minute are reminded to release parking spot: #reminded"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient()
      def secondClientId = registerClient()
    and:
      currentTimeIs(22)
      parkingFacade.occupy(new OccupantId(firstClientId.value()), parkingSpotId, new SpotUnits(2)).orElseThrow()
      def released = parkingFacade.occupy(new OccupantId(secondClientId.value()), parkingSpotId, new SpotUnits(2)).orElseThrow()
      parkingFacade.release(released)

    when:
      currentTimeIs(hour, minute)
      def result = remindingAboutParkingClosingUseCase.run()

    then:
      result == (reminded ? 1 : 0)

    where: // hour 24+ = next day
      hour | minute || reminded
      23   | 59     || false
      24   | 0      || true
      24   | 59     || true
      25   | 0      || false
  }

  def "vehicles left on parking at #hour:#minute are towed: #towed"() {
    given:
      def clientIds = [registerClient(), registerClient()]
    and:
      currentTimeIs(22)
      clientIds.each { clientId ->
        parkingFacade.occupy(new OccupantId(clientId.value()), addParkingSpot(), new SpotUnits(4)).orElseThrow()
      }

    when:
      currentTimeIs(hour, minute)
      def result = towingVehiclesAfterClosingUseCase.run()

    then:
      result == (towed ? 2 : 0)
      parkingFacade.findAllOccupations().size() == (towed ? 0 : 2)

    where: // hour 24+ = next day
      hour | minute || towed
      24   | 59     || false
      25   | 0      || true
      28   | 59     || true
      29   | 0      || false
  }

  def "towed occupant can occupy parking spot again after opening"() {
    given:
      def parkingSpotId = addParkingSpot()
      def clientId = registerClient()
    and:
      currentTimeIs(22)
      parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
    and:
      currentTimeIs(25)
      towingVehiclesAfterClosingUseCase.run()

    when:
      currentTimeIs(29)
      def result = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4))

    then:
      result.isPresent()
  }

}
