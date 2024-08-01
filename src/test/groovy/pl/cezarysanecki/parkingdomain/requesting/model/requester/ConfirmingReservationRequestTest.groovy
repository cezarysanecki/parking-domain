package pl.cezarysanecki.parkingdomain.requesting.model.requester

import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsTimeSlotId
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequest
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

class ConfirmingReservationRequestTest extends Specification {
  
  def "allow to confirm reservation request"() {
    given:
      def requesterId = RequesterId.newOne()
      def timeSlotId = ReservationRequestsTimeSlotId.newOne()
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = new ReservationRequest(requesterId, timeSlotId, spotUnits)
    
    when:
      def result = reservationRequest.confirm()
    
    then:
      result.with {
        assert it.reservationRequest().requesterId == requesterId
        assert it.reservationRequest().reservationRequestId == reservationRequest.reservationRequestId
        assert it.reservationRequest().spotUnits == spotUnits
      }
  }
  
}
