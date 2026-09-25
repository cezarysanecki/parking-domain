package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase.RemindingAboutParkingClosingUseCase
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased
import pl.cezarysanecki.parkingdomain.parking.usecase.CallingTowingServiceAfterClosingUseCase
import pl.cezarysanecki.parkingdomain.parking.usecase.RemoveOccupationByForceUseCase
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class ClosingParkingAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  RemindingAboutParkingClosingUseCase remindingAboutParkingClosingUseCase
  @Autowired
  CallingTowingServiceAfterClosingUseCase callingTowingServiceAfterClosingUseCase
  @Autowired
  RemoveOccupationByForceUseCase removeOccupationByForceUseCase

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

  def "tow truck is called at #hour:#minute for vehicles left on parking: #called"() {
    given:
      def clientIds = [registerClient(), registerClient()]
    and:
      currentTimeIs(22)
      clientIds.each { clientId ->
        parkingFacade.occupy(new OccupantId(clientId.value()), addParkingSpot(), new SpotUnits(4)).orElseThrow()
      }

    when:
      currentTimeIs(hour, minute)
      def result = callingTowingServiceAfterClosingUseCase.run()

    then:
      result == (called ? 2 : 0)
    and: "calling tow truck does not free parking spots"
      parkingFacade.findAllOccupations().size() == 2

    where: // hour 24+ = next day
      hour | minute || called
      24   | 59     || false
      25   | 0      || true
      28   | 59     || true
      29   | 0      || false
  }

  def "parking spot is free only after tow truck confirms that vehicle was towed"() {
    given:
      def parkingSpotId = addParkingSpot()
      def clientId = registerClient()
    and:
      currentTimeIs(22)
      def occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
    and:
      currentTimeIs(25)
      callingTowingServiceAfterClosingUseCase.run()

    expect:
      parkingFacade.findAllOccupations() == [occupationId]

    when:
      def result = removeOccupationByForceUseCase.run(occupationId, ParkingSpotForceReleased.Reason.VEHICLE_TOWED)

    then:
      result
      parkingFacade.findAllOccupations().isEmpty()
  }

  def "occupant of towed vehicle can occupy parking spot again after opening"() {
    given:
      def parkingSpotId = addParkingSpot()
      def clientId = registerClient()
    and:
      currentTimeIs(22)
      def occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
    and:
      currentTimeIs(25)
      removeOccupationByForceUseCase.run(occupationId, ParkingSpotForceReleased.Reason.VEHICLE_TOWED)

    when:
      currentTimeIs(29)
      def result = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4))

    then:
      result.isPresent()
  }

}
