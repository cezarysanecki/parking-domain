package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot

import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static TimeSlotReservationRequestsFixture.timeSlotWithRequest

class CancellingParkingSpotRequestTest extends Specification {
  
  def "allow to cancel parking spot reservation request"() {
    given:
      def requesterId = ReservationRequesterId.newOne()
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = ReservationRequest.newOne(requesterId, spotUnits)
    and:
      def parkingSpotReservationRequests = timeSlotWithRequest(reservationRequest)
    
    when:
      def result = parkingSpotReservationRequests.remove(reservationRequest.reservationRequestId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.reservationRequest().reservationRequesterId == requesterId
        assert it.reservationRequest().reservationRequestId == reservationRequest.reservationRequestId
        assert it.reservationRequest().spotUnits == spotUnits
      }
  }
  
  def "reject cancelling reservation request for parking spot when this is no such request for that parking spot"() {
    given:
      def requesterId = ReservationRequesterId.newOne()
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = ReservationRequest.newOne(requesterId, spotUnits)
    and:
      def parkingSpotReservationRequests = timeSlotWithRequest(reservationRequest)
    
    when:
      def result = parkingSpotReservationRequests.remove(ReservationRequestId.newOne())
    
    then:
      result.isFailure()
  }
  
}
