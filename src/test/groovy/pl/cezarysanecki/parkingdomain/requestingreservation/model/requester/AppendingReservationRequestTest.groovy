package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester

import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification

import static ReservationRequesterFixture.requesterWith
import static ReservationRequesterFixture.requesterWithNoReservationRequests

class AppendingReservationRequestTest extends Specification {
  
  def "allow to append reservation request to requester"() {
    given:
      def requester = requesterWithNoReservationRequests()
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = ReservationRequest.newOne(requester.requesterId, spotUnits)
    
    when:
      def result = requester.append(reservationRequest)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.reservationRequesterId == requester.requesterId
        assert it.reservationRequestId == reservationRequest.reservationRequestId
        assert it.spotUnits == spotUnits
      }
  }
  
  def "reject appending reservation request when limit of requester requests is reached"() {
    given:
      def requester = requesterWith(ReservationRequestId.newOne())
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = ReservationRequest.newOne(requester.requesterId, spotUnits)
    
    when:
      def result = requester.append(reservationRequest)
    
    then:
      result.isFailure()
  }
  
}
