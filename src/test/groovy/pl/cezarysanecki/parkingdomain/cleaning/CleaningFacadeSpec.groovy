package pl.cezarysanecki.parkingdomain.cleaning

import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import spock.lang.Specification

class CleaningFacadeSpec extends Specification {

  // business.cleaning.number-of-drives-away-to-consider-parking-spot-dirty
  static final int DRIVES_AWAY_TO_CONSIDER_DIRTY = 2

  def cleaningRepository = new InMemoryCleaningRepository()
  def externalCleaningService = Mock(ExternalCleaningService)
  def cleaningFacade = new CleaningFacade(cleaningRepository, externalCleaningService, DRIVES_AWAY_TO_CONSIDER_DIRTY)

  def setup() {
    InMemoryRepositories.clearAll()
  }

  def "parking spot released #releases time(s) is dirty: #dirty"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      releases.times { cleaningRepository.increaseCounterFor(parkingSpotId) }

    expect:
      cleaningFacade.getDirtyParkingSpots().contains(parkingSpotId) == dirty

    where:
      releases || dirty
      0        || false
      1        || false
      2        || true
      3        || true
  }

  def "releases are counted per parking spot"() {
    given:
      def dirtySpot = new ParkingSpotId(UUID.randomUUID())
      def cleanSpot = new ParkingSpotId(UUID.randomUUID())
      2.times { cleaningRepository.increaseCounterFor(dirtySpot) }
      cleaningRepository.increaseCounterFor(cleanSpot)

    expect:
      cleaningFacade.getDirtyParkingSpots() == [dirtySpot]
  }

  def "calling cleaning calls external cleaning service"() {
    when:
      def result = cleaningFacade.callCleaning()

    then:
      result == Result.Success
      1 * externalCleaningService.call()
  }

  def "marking cleaning as done resets all counters"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      2.times { cleaningRepository.increaseCounterFor(parkingSpotId) }

    when:
      def result = cleaningFacade.markCleaningAsDone()

    then:
      result == Result.Success
      cleaningFacade.getDirtyParkingSpots().isEmpty()

    when:
      cleaningRepository.increaseCounterFor(parkingSpotId)

    then:
      cleaningFacade.getDirtyParkingSpots().isEmpty()

    when:
      cleaningRepository.increaseCounterFor(parkingSpotId)

    then:
      cleaningFacade.getDirtyParkingSpots() == [parkingSpotId]
  }

}
