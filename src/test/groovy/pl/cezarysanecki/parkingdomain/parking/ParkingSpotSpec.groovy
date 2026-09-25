package pl.cezarysanecki.parkingdomain.parking

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification

class ParkingSpotSpec extends Specification {

  def parkingSpotId = new ParkingSpotId(UUID.randomUUID())

  def "newly created parking spot is empty, without reservations and with zero version"() {
    when:
      def parkingSpot = ParkingSpot.create(parkingSpotId, ParkingSpotCapacity.defaultCapacity())

    then:
      parkingSpot.parkingSpotId() == parkingSpotId
      parkingSpot.occupiedSpace() == 0
      parkingSpot.reservations().isEmpty()
      parkingSpot.capacity() == ParkingSpotCapacity.defaultCapacity()
      parkingSpot.version() == Version.zero()
  }

  def "occupying #units units of spot with capacity 4, #occupied occupied and #reserved reserved is #expected"() {
    given:
      def reservations = reserved == 0 ? [] : [new Reservation(randomReservationId(), new SpotUnits(reserved))]
      def parkingSpot = parkingSpotWith(4, occupied, reservations)

    expect:
      parkingSpot.occupyBy(new SpotUnits(units)) == expected

    where:
      occupied | reserved | units || expected
      0        | 0        | 4     || true
      0        | 0        | 1     || true
      2        | 0        | 2     || true
      3        | 0        | 1     || true
      4        | 0        | 1     || false
      2        | 0        | 4     || false
      0        | 4        | 1     || false
      0        | 2        | 2     || true
      0        | 2        | 4     || false
      2        | 2        | 1     || false
      0        | 0        | 8     || false
  }

  def "can occupy parking spot using its reservation when reserved units fit"() {
    given:
      def reservationId = randomReservationId()
      def reservation = new Reservation(reservationId, new SpotUnits(2))
      def parkingSpot = parkingSpotWith(4, 2, [reservation])

    when:
      def result = parkingSpot.occupyBy(reservationId)

    then:
      result == Optional.of(reservation)
  }

  def "cannot occupy parking spot using reservation not assigned to it"() {
    given:
      def parkingSpot = parkingSpotWith(4, 0, [new Reservation(randomReservationId(), new SpotUnits(2))])

    expect:
      parkingSpot.occupyBy(randomReservationId()).isEmpty()
  }

  def "cannot occupy parking spot using reservation when occupied space plus reserved units exceed capacity"() {
    given:
      def reservationId = randomReservationId()
      def parkingSpot = parkingSpotWith(4, 3, [new Reservation(reservationId, new SpotUnits(2))])

    expect:
      parkingSpot.occupyBy(reservationId).isEmpty()
  }

  def "occupying using reservation ignores other reservations of the parking spot (current behavior)"() {
    given:
      def reservationId = randomReservationId()
      def parkingSpot = parkingSpotWith(4, 0, [
          new Reservation(reservationId, new SpotUnits(4)),
          new Reservation(randomReservationId(), new SpotUnits(4))])

    expect:
      parkingSpot.occupyBy(reservationId).isPresent()
  }

  private ParkingSpot parkingSpotWith(int capacity, int occupied, List<Reservation> reservations) {
    return new ParkingSpot(parkingSpotId, occupied, reservations, new ParkingSpotCapacity(capacity), Version.zero())
  }

  private static ReservationId randomReservationId() {
    return new ReservationId(UUID.randomUUID())
  }

}
