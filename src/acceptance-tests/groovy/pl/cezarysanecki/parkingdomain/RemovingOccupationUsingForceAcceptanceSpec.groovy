package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased
import pl.cezarysanecki.parkingdomain.parking.usecase.RemoveOccupationByForceUseCase
import pl.cezarysanecki.parkingdomain.shared.VehicleType

class RemovingOccupationUsingForceAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  RemoveOccupationByForceUseCase removeOccupationByForceUseCase

  def "occupation can be removed by force"() {
    given:
      def parkingSpotId = addParkingSpot()
      def clientId = registerClient()

    when:
      def occupation = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, VehicleType.CAR).orElseThrow()
      def result = removeOccupationByForceUseCase.run(occupation, ParkingSpotForceReleased.Reason.NOT_RELEASED_PARKING_SPOT)

    then:
      result == true
  }

}
