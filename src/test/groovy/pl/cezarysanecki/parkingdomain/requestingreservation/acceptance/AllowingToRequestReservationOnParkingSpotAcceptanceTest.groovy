package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.acceptance.AbstractParkingAcceptanceTest
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequestsViewRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits

class AllowingToRequestReservationOnParkingSpotAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  MakingReservationRequest storingReservationRequest
  @Autowired
  ReservationRequestsViewRepository parkingSpotReservationRequestsViewRepository
  @Autowired
  ReservationRequesterViewRepository reservationRequesterViewRepository
  
  ParkingSpotId parkingSpotId
  ReservationRequesterId requesterId
  
  def setup() {
    def clientId = registerBeneficiary()
    
    parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    requesterId = ReservationRequesterId.of(clientId.value)
    
    createTimeSlots()
  }
  
  def "request reservation for parking spot"() {
    given:
      def parkingSpotTimeSlotId = findAnyFullyFreeParkingSpotTimeSlot(parkingSpotId)
    
    when:
      def reservationRequest = storingReservationRequest.makeRequest(
          requesterId, parkingSpotTimeSlotId, SpotUnits.of(2))
    
    then:
      parkingSpotTimeSlotHasSpaceLeft(parkingSpotTimeSlotId, 2)
    and:
      requesterContainsReservationRequest(requesterId, reservationRequest.get().reservationRequestId)
  }
  
  def "request reservation for whole parking spot"() {
    given:
      def parkingSpotTimeSlotId = findAnyFullyFreeParkingSpotTimeSlot(parkingSpotId)
    
    when:
      def reservationRequest = storingReservationRequest.makeRequest(
          requesterId, parkingSpotTimeSlotId, SpotUnits.of(4))
    
    then:
      parkingSpotTimeSlotHasNoSpaceLeft(parkingSpotTimeSlotId)
    and:
      requesterContainsReservationRequest(requesterId, reservationRequest.get().reservationRequestId)
  }
  
  private ReservationRequestsTimeSlotId findAnyFullyFreeParkingSpotTimeSlot(ParkingSpotId parkingSpotId) {
    return ReservationRequestsTimeSlotId.of(
        parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
            .find { it.parkingSpotId() == parkingSpotId.value && it.spaceLeft() == it.capacity() }
            .timeSlotId())
  }
  
  private void requesterContainsReservationRequest(ReservationRequesterId requesterId, ReservationRequestId reservationRequestId) {
    reservationRequesterViewRepository.queryForAllReservationRequesters()
        .find { it -> it.requesterId() == requesterId.value }
        .with {
          assert it.reservationRequests().contains(reservationRequestId.value)
        }
  }
  
  private void parkingSpotTimeSlotHasSpaceLeft(ReservationRequestsTimeSlotId parkingSpotTimeSlotId, Integer spaceLeft) {
    parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
        .find { it -> it.timeSlotId() == parkingSpotTimeSlotId.value }
        .with {
          assert it.spaceLeft() == spaceLeft
        }
  }
  
  private void parkingSpotTimeSlotHasNoSpaceLeft(ReservationRequestsTimeSlotId parkingSpotTimeSlotId) {
    parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
        .each { it -> it.timeSlotId() != parkingSpotTimeSlotId.value }
  }
  
}
