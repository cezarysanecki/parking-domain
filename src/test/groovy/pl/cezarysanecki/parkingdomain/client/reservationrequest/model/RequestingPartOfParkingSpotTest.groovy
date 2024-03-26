package pl.cezarysanecki.parkingdomain.client.reservationrequest.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod
import spock.lang.Specification

import java.time.LocalDateTime

import static ClientReservationRequestsEvent.ReservationRequestFailed
import static ClientReservationRequestsFixture.anyClientId
import static ClientReservationRequestsFixture.noReservationRequests
import static ClientReservationRequestsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.AnyParkingSpotReservationRequested
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId

class RequestingPartOfParkingSpotTest extends Specification {
  
  ClientId clientId = anyClientId()
  
  LocalDateTime now = LocalDateTime.now()
  
  def "can make reservation for part of parking spot"() {
    given:
      def clientReservationRequests = noReservationRequests(clientId, now)
    and:
      def reservationPeriod = ReservationPeriod.morning()
      def parkingSpotType = ParkingSpotType.Bronze
      def vehicleSizeUnit = VehicleSizeUnit.of(2)
    
    when:
      Either<ReservationRequestFailed, AnyParkingSpotReservationRequested> result = clientReservationRequests.reserve(reservationPeriod, parkingSpotType, vehicleSizeUnit)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientId
        assert it.reservationPeriod == reservationPeriod
        assert it.parkingSpotType == parkingSpotType
        assert it.vehicleSizeUnit == vehicleSizeUnit
      }
  }
  
  def "cannot make reservation for part of parking spot when there is too many made reservations"() {
    given:
      def clientReservations = reservationRequestsWith(clientId, anyReservationId(), now)
    
    when:
      Either<ReservationRequestFailed, ChosenParkingSpotReservationRequested> result = clientReservations.reserve(ReservationPeriod.morning(), anyParkingSpotId())
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "cannot have more reservations"
      }
  }
  
}
