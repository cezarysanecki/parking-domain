package pl.cezarysanecki.parkingdomain.cleaning.usecase

import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories
import pl.cezarysanecki.parkingdomain._local.LocalDateProvider
import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade
import pl.cezarysanecki.parkingdomain.cleaning.ExternalCleaningService
import pl.cezarysanecki.parkingdomain.cleaning.InMemoryCleaningRepository
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import spock.lang.Specification

import java.time.LocalDate

class CallingCleaningWhenSpotsDirtyUseCaseSpec extends Specification {

  // business.cleaning.number-of-dirty-parking-spots-to-call-external-service
  static final int DIRTY_SPOTS_TO_CALL_CLEANING = 10
  // when a single release makes a spot dirty is covered by CleaningFacadeSpec
  static final int DRIVES_AWAY_TO_CONSIDER_DIRTY = 1
  static final LocalDate CURRENT_DATE = LocalDate.of(2020, 10, 10)

  def cleaningRepository = new InMemoryCleaningRepository()
  def externalCleaningService = Mock(ExternalCleaningService)
  def dateProvider = new LocalDateProvider()
  def cleaningFacade = new CleaningFacade(cleaningRepository, externalCleaningService, dateProvider, DRIVES_AWAY_TO_CONSIDER_DIRTY)
  def useCase = new CallingCleaningWhenSpotsDirtyUseCase(cleaningFacade, DIRTY_SPOTS_TO_CALL_CLEANING)

  def setup() {
    InMemoryRepositories.clearAll()
    dateProvider.setCurrentDate(CURRENT_DATE)
    dateProvider.passMinutes(90) // 1:30, during technical break
  }

  def "with #dirtySpots dirty parking spot(s) result is #expected and external cleaning service is called #calls time(s)"() {
    given:
      dirtySpots.times { cleaningRepository.increaseCounterFor(new ParkingSpotId(UUID.randomUUID())) }

    when:
      def result = useCase.run()

    then:
      result == expected
      calls * externalCleaningService.call()

    where:
      dirtySpots || expected         | calls
      0          || Result.Rejection | 0
      9          || Result.Rejection | 0
      10         || Result.Success   | 1
      11         || Result.Success   | 1
  }

}
