package pl.cezarysanecki.parkingdomain.cleaning.usecase

import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import spock.lang.Specification

class CallingCleaningWhenSpotsDirtyUseCaseSpec extends Specification {

  static final int DIRTY_SPOTS_TO_CALL_CLEANING = 10

  def cleaningFacade = Mock(CleaningFacade)
  def useCase = new CallingCleaningWhenSpotsDirtyUseCase(cleaningFacade, DIRTY_SPOTS_TO_CALL_CLEANING)

  def "with #dirtySpots dirty parking spot(s) result is #expected and cleaning is called #calls time(s)"() {
    given:
      cleaningFacade.getDirtyParkingSpots() >> (0..<dirtySpots).collect { new ParkingSpotId(UUID.randomUUID()) }

    when:
      def result = useCase.run()

    then:
      result == expected
      calls * cleaningFacade.callCleaning()

    where:
      dirtySpots || expected         | calls
      0          || Result.Rejection | 0
      9          || Result.Rejection | 0
      10         || Result.Success   | 1
      11         || Result.Success   | 1
  }

}
