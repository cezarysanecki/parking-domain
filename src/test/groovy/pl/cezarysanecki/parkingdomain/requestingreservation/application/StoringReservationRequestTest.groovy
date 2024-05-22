package pl.cezarysanecki.parkingdomain.requestingreservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestStored
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsFixture.parkingSpotWithoutReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterFixture.requesterWith
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterFixture.requesterWithNoReservationRequests

class StoringReservationRequestTest extends Specification {
  
  ReservationRequesterRepository reservationRequesterRepository = Mock()
  ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository = Mock()
  
  @Subject
  StoringReservationRequest storingReservationRequest = new StoringReservationRequest(
      reservationRequesterRepository,
      parkingSpotReservationRequestsRepository)
  
  def "should store reservation request for requester"() {
    given:
      def requester = requesterWithNoReservationRequests(1)
      reservationRequesterRepository.findBy(requester.requesterId) >> Option.of(requester)
    and:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotTimeSlotId) >> Option.of(parkingSpotReservationRequests)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = storingReservationRequest.storeRequest(requester.requesterId, parkingSpotReservationRequests.parkingSpotTimeSlotId, spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.reservationRequesterId == requester.requesterId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * reservationRequesterRepository.save(requester)
      1 * parkingSpotReservationRequestsRepository.publish(_ as ReservationRequestStored)
  }
  
  def "fail to store reservation request when requester does not have enough limit"() {
    given:
      def requester = requesterWith(ReservationRequestId.newOne())
      reservationRequesterRepository.findBy(requester.requesterId) >> Option.of(requester)
    and:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotTimeSlotId) >> Option.of(parkingSpotReservationRequests)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = storingReservationRequest.storeRequest(requester.requesterId, parkingSpotReservationRequests.parkingSpotTimeSlotId, spotUnits)
    
    then:
      result.isFailure()
  }
  
}
