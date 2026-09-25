package pl.cezarysanecki.parkingdomain.requesting

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import spock.lang.Specification

import java.time.LocalDate

class RequestableParkingSpotSpec extends Specification {

  def timeSlot = TimeSlot.create(LocalDate.of(2020, 10, 10), 10, 15)

  def "newly created requestable parking spot has nothing requested"() {
    when:
      def spot = RequestableParkingSpot.createNew(new ParkingSpotId(UUID.randomUUID()), 4, timeSlot)

    then:
      spot.occupiedSpots() == 0
      spot.capacity() == 4
      spot.timeSlot() == timeSlot
      spot.version() == Version.zero()
  }

  def "requesting #units units with capacity #capacity and #occupied already requested is possible: #expected"() {
    given:
      def spot = new RequestableParkingSpot(new ParkingSpotId(UUID.randomUUID()), capacity, occupied, timeSlot, Version.zero())

    expect:
      spot.canRequestFor(new SpotUnits(units)) == expected

    where:
      capacity | occupied | units || expected
      4        | 0        | 4     || true
      4        | 2        | 2     || true
      4        | 2        | 4     || false
      4        | 4        | 1     || false
  }

}
