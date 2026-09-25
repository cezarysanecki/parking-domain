package pl.cezarysanecki.parkingdomain.cleaning

import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories
import pl.cezarysanecki.parkingdomain._local.LocalDateProvider
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class CleaningFacadeSpec extends Specification {

  // business.cleaning.number-of-drives-away-to-consider-parking-spot-dirty
  static final int DRIVES_AWAY_TO_CONSIDER_DIRTY = 20
  static final LocalDate CURRENT_DATE = LocalDate.of(2020, 10, 10)

  def cleaningRepository = new InMemoryCleaningRepository()
  def externalCleaningService = Mock(ExternalCleaningService)
  def dateProvider = new LocalDateProvider()
  def cleaningFacade = new CleaningFacade(cleaningRepository, externalCleaningService, dateProvider, DRIVES_AWAY_TO_CONSIDER_DIRTY)

  def setup() {
    InMemoryRepositories.clearAll()
    dateProvider.setCurrentDate(CURRENT_DATE)
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
      19       || false
      20       || true
      21       || true
  }

  def "releases are counted per parking spot"() {
    given:
      def dirtySpot = new ParkingSpotId(UUID.randomUUID())
      def cleanSpot = new ParkingSpotId(UUID.randomUUID())
      20.times { cleaningRepository.increaseCounterFor(dirtySpot) }
      19.times { cleaningRepository.increaseCounterFor(cleanSpot) }

    expect:
      cleaningFacade.getDirtyParkingSpots() == [dirtySpot]
  }

  def "calling cleaning at #time is #expected because parking spots can be cleaned only during technical break (1:00-5:00)"() {
    given:
      dateProvider.passMinutes(time.toSecondOfDay().intdiv(60))

    when:
      def result = cleaningFacade.callCleaning()

    then:
      result == expected
      calls * externalCleaningService.call()

    where:
      time                  || expected         | calls
      LocalTime.of(0, 0)    || Result.Rejection | 0
      LocalTime.of(0, 59)   || Result.Rejection | 0
      LocalTime.of(1, 0)    || Result.Success   | 1
      LocalTime.of(1, 30)   || Result.Success   | 1
      LocalTime.of(4, 59)   || Result.Success   | 1
      LocalTime.of(5, 0)    || Result.Rejection | 0
      LocalTime.of(13, 0)   || Result.Rejection | 0
      LocalTime.of(23, 59)  || Result.Rejection | 0
  }

  def "marking cleaning as done resets all counters"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      20.times { cleaningRepository.increaseCounterFor(parkingSpotId) }

    when:
      def result = cleaningFacade.markCleaningAsDone()

    then:
      result == Result.Success
      cleaningFacade.getDirtyParkingSpots().isEmpty()

    when:
      19.times { cleaningRepository.increaseCounterFor(parkingSpotId) }

    then:
      cleaningFacade.getDirtyParkingSpots().isEmpty()

    when:
      cleaningRepository.increaseCounterFor(parkingSpotId)

    then:
      cleaningFacade.getDirtyParkingSpots() == [parkingSpotId]
  }

}
