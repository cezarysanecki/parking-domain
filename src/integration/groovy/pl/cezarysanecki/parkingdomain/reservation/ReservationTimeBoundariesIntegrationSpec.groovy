package pl.cezarysanecki.parkingdomain.reservation

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.shared.TimeSlot

import java.time.Duration
import java.time.Instant

class ReservationTimeBoundariesIntegrationSpec extends BaseIntegrationSpec {

  // business.parking.minutes-to-consider-reservation-active
  static final int MINUTES_TO_CONSIDER_RESERVATION_ACTIVE = 60
  // business.parking.minutes-to-consider-reservation-not-used
  static final int MINUTES_TO_CONSIDER_RESERVATION_NOT_USED = 15
  // far in the future: Quartz reservation jobs run on the real clock and never pick these reservations up
  static final Instant NOW = Instant.parse("2100-01-01T10:00:00Z")

  @Autowired
  ReservationRepository reservationRepository

  def "reservation starting in #minutesToStart minutes is activated: #activated"() {
    given:
      def reservation = reservationStartingAt(NOW + minutes(minutesToStart))
      reservationRepository.saveAll([reservation])

    when:
      def result = reservationRepository.loadAllStaleSince(NOW + minutes(MINUTES_TO_CONSIDER_RESERVATION_ACTIVE))

    then:
      (reservation in result) == activated

    where:
      minutesToStart || activated
      59             || true
      60             || true
      61             || false
  }

  def "active reservation started #minutesAfterStart minutes ago is not used: #notUsed"() {
    given:
      def reservation = reservationStartingAt(NOW - minutes(minutesAfterStart))
      reservationRepository.saveAll([reservation])
      reservationRepository.markAsActive([reservation.reservationId()])

    when:
      def result = reservationRepository.loadAllActiveSince(NOW - minutes(MINUTES_TO_CONSIDER_RESERVATION_NOT_USED))

    then:
      (reservation in result) == notUsed

    where:
      minutesAfterStart || notUsed
      14                || false
      15                || false
      16                || true
  }

  private static Duration minutes(int value) {
    return Duration.ofMinutes(value)
  }

  private static Reservation reservationStartingAt(Instant from) {
    return new Reservation(
        new ReservationId(UUID.randomUUID()),
        new ReservationOwnerId(UUID.randomUUID()),
        new ParkingSpotId(UUID.randomUUID()),
        new TimeSlot(from, from + Duration.ofHours(5)),
        new SpotUnits(4))
  }

}
