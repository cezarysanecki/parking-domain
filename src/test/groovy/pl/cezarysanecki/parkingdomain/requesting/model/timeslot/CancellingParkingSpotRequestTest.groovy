package pl.cezarysanecki.parkingdomain.requesting.model.timeslot

import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId

import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

class CancellingParkingSpotRequestTest extends Specification {
  
  def "allow to cancel reservation request"() {
    given:
      def requesterId = RequesterId.newOne()
      def timeSlotId = ReservationRequestsTimeSlotId.newOne()
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = new ReservationRequest(requesterId, timeSlotId, spotUnits)
    
    when:
      def result = reservationRequest.cancel()
    
    then:
      result.with {
        assert it.reservationRequest().requesterId == requesterId
        assert it.reservationRequest().reservationRequestId == reservationRequest.requestId
        assert it.reservationRequest().spotUnits == spotUnits
      }
  }
  
}
