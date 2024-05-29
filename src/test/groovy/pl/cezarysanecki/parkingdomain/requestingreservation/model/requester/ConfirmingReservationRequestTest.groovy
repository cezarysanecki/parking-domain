package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester

import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

class ConfirmingReservationRequestTest extends Specification {
  
  def "allow to confirm reservation request"() {
    given:
      def requesterId = ReservationRequesterId.newOne()
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
