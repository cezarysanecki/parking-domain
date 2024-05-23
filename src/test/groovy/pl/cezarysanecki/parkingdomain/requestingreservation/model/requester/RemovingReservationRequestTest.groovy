package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester

import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static ReservationRequesterFixture.requesterWithNoReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterFixture.requesterWith

class RemovingReservationRequestTest extends Specification {
  
  def "allow to remove reservation request from requester"() {
    given:
      def reservationRequestId = ReservationRequestId.newOne()
      def requester = requesterWith(reservationRequestId)
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = new ReservationRequest(requester.requesterId, reservationRequestId, spotUnits)
    
    when:
      def result = requester.remove(reservationRequest.reservationRequestId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it == reservationRequest.reservationRequestId
      }
  }
  
  def "reject removing reservation request when there is no such"() {
    given:
      def requester = requesterWithNoReservationRequests(1)
    
    when:
      def result = requester.remove(ReservationRequestId.newOne())
    
    then:
      result.isFailure()
  }
  
}
