package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.cleaning.usecase.CallingCleaningWhenSpotsDirtyUseCase
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.shared.VehicleType

class CallingExternalCleaningAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  CallingCleaningWhenSpotsDirtyUseCase callingCleaningWhenSpotsDirtyUseCase

  def "call cleaning if there is required number of dirty parking spots"() {
    given:
      def clientId = registerClient()

    when:
      10.times {
        def parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Silver)
        20.times {
          def occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, VehicleType.CAR).orElseThrow()
          parkingFacade.release(occupationId)
        }
      }
    and:
      def result = callingCleaningWhenSpotsDirtyUseCase.run()

    then:
      result == Result.Success
  }

}
