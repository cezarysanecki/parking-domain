package pl.cezarysanecki.parkingdomain.cleaning.usecase

import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import spock.lang.Specification

class CallingCleaningWhenSpotsDirtyUseCaseSpec extends Specification {

  // business.cleaning.number-of-dirty-parking-spots-to-call-external-service
  static final int DIRTY_SPOTS_TO_CALL_CLEANING = 10

  def cleaningFacade = Mock(CleaningFacade)
  def useCase = new CallingCleaningWhenSpotsDirtyUseCase(cleaningFacade, DIRTY_SPOTS_TO_CALL_CLEANING)

  def "with #dirtySpots dirty parking spot(s) result is #expected and cleaning is called #calls time(s)"() {
    given:
      cleaningFacade.getDirtyParkingSpots() >> dirtyParkingSpots(dirtySpots)

    when:
      def result = useCase.run()

    then:
      result == expected
      calls * cleaningFacade.callCleaning() >> Result.Success

    where:
      dirtySpots || expected         | calls
      0          || Result.Rejection | 0
      9          || Result.Rejection | 0
      10         || Result.Success   | 1
      11         || Result.Success   | 1
  }

  def "result is rejection when cleaning facade rejects calling cleaning (outside technical break)"() {
    given:
      cleaningFacade.getDirtyParkingSpots() >> dirtyParkingSpots(DIRTY_SPOTS_TO_CALL_CLEANING)

    when:
      def result = useCase.run()

    then:
      result == Result.Rejection
      1 * cleaningFacade.callCleaning() >> Result.Rejection
  }

  private static List<ParkingSpotId> dirtyParkingSpots(int count) {
    return (0..<count).collect { new ParkingSpotId(UUID.randomUUID()) }
  }

}
