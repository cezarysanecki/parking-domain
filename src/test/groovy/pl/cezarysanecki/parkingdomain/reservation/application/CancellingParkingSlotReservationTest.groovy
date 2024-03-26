package pl.cezarysanecki.parkingdomain.reservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository
import pl.cezarysanecki.parkingdomain.reservation.model.Reservation
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.anyReservationId

class CancellingParkingSlotReservationTest extends Specification {
  
  ClientId clientId = anyClientId()
  ReservationId reservationId = anyReservationId()
  
  ParkingSpotReservationsRepository repository = Stub()
  
  def 'should successfully cancel reservation'() {
    given:
      CancellingReservationEventListener cancellingReservationEventListener = new CancellingReservationEventListener(repository)
    
    when:
      def result = cancellingReservationEventListener.handle(new ReservationRequestCancelled(clientId, reservationId))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject cancelling reservation when its to late'() {
    given:
      CancellingReservation cancellingReservation = new CancellingReservation(repository)
    and:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationId = anyReservationId()
      def reservation = new pl.cezarysanecki.parkingdomain.reservation.model.Reservation(reservationId, new ReservationSlot(now.plusMinutes(30), 3), clientId)
    and:
      persisted(reservationId, reservationScheduleWith(now, reservation))
    
    when:
      def result = cancellingReservation.cancel(new CancelReservationCommand(reservationId))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  ParkingSpotReservations persisted(ReservationId reservationId, ParkingSpotReservations parkingSpotReservations) {
    repository.findBy(reservationId) >> Option.of(parkingSpotReservations)
    repository.publish(_ as ParkingSpotReservationsEvent) >> parkingSpotReservations
    return parkingSpotReservations
  }
  
  void unknownReservationSchedule(ReservationId reservationId) {
    repository.findBy(reservationId) >> Option.none()
  }
  
}
