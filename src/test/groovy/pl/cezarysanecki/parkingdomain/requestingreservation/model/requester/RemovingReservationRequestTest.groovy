package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester

import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
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
      def result = requester.remove(reservationRequest)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.reservationRequesterId == requester.requesterId
        assert it.reservationRequestId == reservationRequest.reservationRequestId
        assert it.spotUnits == spotUnits
      }
  }
  
  def "reject removing reservation request when there is no such"() {
    given:
      def requester = requesterWithNoReservationRequests()
    
    when:
      def result = requester.remove(ReservationRequest.newOne(requester.requesterId, SpotUnits.of(2)))
    
    then:
      result.isFailure()
  }
  
}
