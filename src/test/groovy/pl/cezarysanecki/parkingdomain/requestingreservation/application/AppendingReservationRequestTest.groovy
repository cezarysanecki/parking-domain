package pl.cezarysanecki.parkingdomain.requestingreservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestsEvents.ReservationRequestAppended
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterFixture.requesterWith
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterFixture.requesterWithNoReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ParkingSpotReservationRequestsFixture.parkingSpotWithoutReservationRequests

class AppendingReservationRequestTest extends Specification {
  
  ReservationRequesterRepository reservationRequesterRepository = Mock()
  ReservationRequestsTimeSlotRepository parkingSpotReservationRequestsRepository = Mock()
  
  @Subject
  MakingReservationRequest storingReservationRequest = new MakingReservationRequest(
      reservationRequesterRepository,
      parkingSpotReservationRequestsRepository)
  
  def "should store reservation request for requester"() {
    given:
      def requester = requesterWithNoReservationRequests(1)
      reservationRequesterRepository.findBy(requester.requesterId) >> Option.of(requester)
    and:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.timeSlotId) >> Option.of(parkingSpotReservationRequests)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = storingReservationRequest.makeRequest(requester.requesterId, parkingSpotReservationRequests.timeSlotId, spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.requesterId == requester.requesterId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * reservationRequesterRepository.save(requester)
      1 * parkingSpotReservationRequestsRepository.publish(_ as ReservationRequestAppended)
  }
  
  def "fail to store reservation request when requester does not have enough limit"() {
    given:
      def requester = requesterWith(ReservationRequestId.newOne())
      reservationRequesterRepository.findBy(requester.requesterId) >> Option.of(requester)
    and:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.timeSlotId) >> Option.of(parkingSpotReservationRequests)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = storingReservationRequest.makeRequest(requester.requesterId, parkingSpotReservationRequests.timeSlotId, spotUnits)
    
    then:
      result.isFailure()
  }
  
}
