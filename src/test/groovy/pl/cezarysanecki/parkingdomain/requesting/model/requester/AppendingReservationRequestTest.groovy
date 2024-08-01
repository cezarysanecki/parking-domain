package pl.cezarysanecki.parkingdomain.requesting.model.requester


import spock.lang.Specification

import static ReservationRequesterFixture.requesterWith
import static ReservationRequesterFixture.requesterWithNoReservationRequests

class AppendingReservationRequestTest extends Specification {
  
  def "allow to append reservation request to requester"() {
    given:
      def requester = requesterWithNoReservationRequests(1)
    and:
      def reservationRequestId = ReservationRequestId.newOne()
    
    when:
      def result = requester.append(reservationRequestId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it == reservationRequestId
      }
  }
  
  def "reject appending reservation request when limit of requester requests is reached"() {
    given:
      def requester = requesterWith(ReservationRequestId.newOne())
    and:
      def reservationRequestId = ReservationRequestId.newOne()
    
    when:
      def result = requester.append(reservationRequestId)
    
    then:
      result.isFailure()
  }
  
}
