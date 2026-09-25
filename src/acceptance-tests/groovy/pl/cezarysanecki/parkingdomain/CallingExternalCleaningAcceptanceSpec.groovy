package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade
import pl.cezarysanecki.parkingdomain.cleaning.usecase.CallingCleaningWhenSpotsDirtyUseCase
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.views.ViewCleaningRepository

class CallingExternalCleaningAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  CallingCleaningWhenSpotsDirtyUseCase callingCleaningWhenSpotsDirtyUseCase
  @Autowired
  CleaningFacade cleaningFacade
  @Autowired
  ViewCleaningRepository viewCleaningRepository

  def "call cleaning if there is required number of dirty parking spots"() {
    given:
      def clientId = registerClient()

    when:
      10.times {
        def parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Silver)
        2.times {
          def occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
          parkingFacade.release(occupationId)
        }
      }
    and:
      def result = callingCleaningWhenSpotsDirtyUseCase.run()

    then:
      result == Result.Success
  }

  def "parking spot released #releases time(s) is dirty: #dirty"() {
    given:
      def clientId = registerClient()
      def parkingSpotId = addParkingSpot()

    when:
      occupyAndRelease(clientId, parkingSpotId, releases)

    then:
      cleaningFacade.getDirtyParkingSpots().contains(parkingSpotId) == dirty
    and:
      viewCleaningRepository.queryCleaning().parkingSpotsExceedingThreshold() == (dirty ? 1 : 0)

    where:
      releases || dirty
      1        || false
      2        || true
      3        || true
  }

  def "#releases releases (#dirtySpots spot(s) twice, #cleanSpots once) make #dirtySpots parking spot(s) dirty"() {
    given:
      def clientId = registerClient()

    when:
      dirtySpots.times { occupyAndRelease(clientId, addParkingSpot(), 2) }
      cleanSpots.times { occupyAndRelease(clientId, addParkingSpot(), 1) }

    then:
      cleaningFacade.getDirtyParkingSpots().size() == dirtySpots
    and:
      viewCleaningRepository.queryCleaning().parkingSpotsExceedingThreshold() == dirtySpots

    where:
      dirtySpots | cleanSpots || releases
      9          | 1          || 19
      10         | 0          || 20
  }

  private void occupyAndRelease(ClientId clientId, ParkingSpotId parkingSpotId, int times) {
    times.times {
      def occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
      parkingFacade.release(occupationId).orElseThrow()
    }
  }

}
