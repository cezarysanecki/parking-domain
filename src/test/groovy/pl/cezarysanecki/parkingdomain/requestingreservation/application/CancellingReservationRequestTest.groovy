package pl.cezarysanecki.parkingdomain.requestingreservation.application

import io.vavr.collection.HashSet
import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsFixture.parkingSpotWithRequest
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsFixture.parkingSpotWithoutReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterFixture.requesterWithNoReservationRequests

class CancellingReservationRequestTest extends Specification {
  
  EventPublisher eventPublisher = Mock()
  ReservationRequesterRepository reservationRequesterRepository = Mock()
  ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository = Mock()
  
  @Subject
  CancellingReservationRequest cancellingReservationRequest = new CancellingReservationRequest(
      eventPublisher,
      reservationRequesterRepository,
      parkingSpotReservationRequestsRepository)
  
  def "should cancel reservation request"() {
    given:
      def requesterId = ReservationRequesterId.newOne()
      def reservationRequestId = ReservationRequestId.newOne()
      def spotUnits = SpotUnits.of(2)
    and:
      def reservationRequest = new ReservationRequest(requesterId, reservationRequestId, spotUnits)
    and:
      def requester = new ReservationRequester(requesterId, HashSet.of(reservationRequestId), 1)
      reservationRequesterRepository.findBy(reservationRequestId) >> Option.of(requester)
    and:
      def parkingSpotReservationRequests = parkingSpotWithRequest(reservationRequest)
      parkingSpotReservationRequestsRepository.findBy(reservationRequestId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      def result = cancellingReservationRequest.cancelRequest(reservationRequestId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.reservationRequesterId == requesterId
        assert it.reservationRequestId == reservationRequestId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * reservationRequesterRepository.save(requester)
      1 * parkingSpotReservationRequestsRepository.save(parkingSpotReservationRequests)
    and:
      1 * eventPublisher.publish(_ as ReservationRequestCancelled)
  }
  
  def "fail to store reservation request when requester does not have enough limit"() {
    given:
      def reservationRequestId = ReservationRequestId.newOne()
    and:
      def requester = requesterWithNoReservationRequests(1)
      reservationRequesterRepository.findBy(reservationRequestId) >> Option.of(requester)
    and:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
      parkingSpotReservationRequestsRepository.findBy(reservationRequestId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      def result = cancellingReservationRequest.cancelRequest(reservationRequestId)
    
    then:
      result.isFailure()
  }
  
}
