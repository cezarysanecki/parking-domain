package pl.cezarysanecki.parkingdomain.cleaning

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.views.ViewCleaningRepository

import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Cleaning.CLEANING

class DirtyParkingSpotIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  DSLContext create
  @Autowired
  CleaningRepository cleaningRepository
  @Autowired
  CleaningFacade cleaningFacade
  @Autowired
  ViewCleaningRepository viewCleaningRepository

  def setup() {
    create.delete(CLEANING).execute()
  }

  def "parking spot released #releases time(s) is dirty: #dirty"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())

    when:
      releases.times { cleaningRepository.increaseCounterFor(parkingSpotId) }

    then:
      cleaningFacade.getDirtyParkingSpots().contains(parkingSpotId) == dirty
    and:
      with(viewCleaningRepository.queryCleaning()) {
        parkingSpotsExceedingThreshold() == (dirty ? 1 : 0)
        records()*.counter() == [releases]
      }

    where:
      releases || dirty
      19       || false
      20       || true
      21       || true
  }

  def "marking cleaning as done resets all counters"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      20.times { cleaningRepository.increaseCounterFor(parkingSpotId) }

    when:
      cleaningFacade.markCleaningAsDone()

    then:
      cleaningFacade.getDirtyParkingSpots().isEmpty()
      viewCleaningRepository.queryCleaning().records().isEmpty()
  }

}
