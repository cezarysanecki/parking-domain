package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyingWithoutAccountUseCase
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class OccupyingParkingSpotAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  OccupyingWithoutAccountUseCase occupyingWithoutAccountUseCase

  def setup() {
    currentTimeIs(DURING_OCCUPYING_HOURS)
  }

  def "cannot occupy parking spot if capacity is exceeded"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient(ClientType.BUSINESS)
      def secondClientId = registerClient(ClientType.BUSINESS)

    when:
      parkingFacade.occupy(new OccupantId(firstClientId.value()), parkingSpotId, new SpotUnits(4))
      def result = parkingFacade.occupy(new OccupantId(secondClientId.value()), parkingSpotId, new SpotUnits(1))

    then:
      result.isEmpty()
  }

  def "can occupy parking spot if previous occupation has been released"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient(ClientType.BUSINESS)
      def secondClientId = registerClient(ClientType.BUSINESS)

    when:
      def occupation = parkingFacade.occupy(new OccupantId(firstClientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
      parkingFacade.release(occupation)
    and:
      def result = parkingFacade.occupy(new OccupantId(secondClientId.value()), parkingSpotId, new SpotUnits(4))

    then:
      result.isPresent()
  }

  def "can occupy parking spot without account but need to pass phone number"() {
    given:
      def parkingSpotId = addParkingSpot()

    when:
      def result = occupyingWithoutAccountUseCase.run(RandomTestUtils.randomPhoneNumber(), parkingSpotId, new SpotUnits(4))

    then:
      result.isPresent()
  }

}
