package pl.cezarysanecki.parkingdomain.requesting.application

import pl.cezarysanecki.parkingdomain.requesting.MakingReservationRequest
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequests
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsEvent
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsRepository

import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requesting.model.requester.ReservationRequesterFixture.requesterWith
import static pl.cezarysanecki.parkingdomain.requesting.model.requester.ReservationRequesterFixture.requesterWithNoReservationRequests
import static pl.cezarysanecki.parkingdomain.requesting.model.timeslot.TimeSlotReservationRequestsFixture.timeSlotWithoutRequests

class AppendingReservationRequestTest extends Specification {
  
  ReservationRequestsRepository reservationRequestsRepository = Mock()
  
  @Subject
  MakingReservationRequest storingReservationRequest = new MakingReservationRequest(
      reservationRequestsRepository)
  
  def "should store reservation request for requester"() {
    given:
      def timeSlot = timeSlotWithoutRequests()
      def requester = requesterWithNoReservationRequests(1)
      reservationRequestsRepository.getBy(requester.requesterId(), timeSlot.timeSlotId()) >> new ReservationRequests(timeSlot, requester)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = storingReservationRequest.makeRequest(requester.requesterId(), timeSlot.timeSlotId(), spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.requesterId == requester.requesterId()
        assert it.spotUnits == spotUnits
      }
    and:
      1 * reservationRequestsRepository.publish(_ as ReservationRequestsEvent.ReservationRequestMade)
  }
  
  def "fail to store reservation request when requester does not have enough limit"() {
    given:
      def timeSlot = timeSlotWithoutRequests()
      def requester = requesterWith(ReservationRequestId.newOne())
      reservationRequestsRepository.getBy(requester.requesterId(), timeSlot.timeSlotId()) >> new ReservationRequests(timeSlot, requester)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = storingReservationRequest.makeRequest(
          requester.requesterId(), timeSlot.timeSlotId(), spotUnits)
    
    then:
      result.isFailure()
  }
  
}
