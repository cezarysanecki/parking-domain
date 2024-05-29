package pl.cezarysanecki.parkingdomain.requestingreservation.application

import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestEvent
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

class RemovingReservationRequestTest extends Specification {
  
  ReservationRequestRepository reservationRequestRepository = Mock()
  
  @Subject
  CancellingReservationRequest cancellingReservationRequest = new CancellingReservationRequest(
      reservationRequestRepository)
  
  def "should cancel reservation request"() {
    given:
      def spotUnits = SpotUnits.of(2)
      def reservationRequest = new ReservationRequest(ReservationRequesterId.newOne(), ReservationRequestsTimeSlotId.newOne(), spotUnits)
    and:
      reservationRequestRepository.getBy(reservationRequest.reservationRequestId) >> reservationRequest
    
    when:
      def result = cancellingReservationRequest.cancelRequest(reservationRequest.reservationRequestId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it == reservationRequest.reservationRequestId
      }
    and:
      1 * reservationRequestRepository.publish(_ as ReservationRequestEvent.ReservationRequestCancelled)
  }
  
}
