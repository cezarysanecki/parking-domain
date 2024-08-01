package pl.cezarysanecki.parkingdomain.requesting.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.acceptance.AbstractParkingAcceptanceTest
import pl.cezarysanecki.parkingdomain.requesting.MakingReservationRequest
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsTimeSlotId

import pl.cezarysanecki.parkingdomain.web.ReservationRequesterViewRepository
import pl.cezarysanecki.parkingdomain.web.ReservationRequestsViewRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits

class AllowingToRequestReservationOnParkingSpotAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  MakingReservationRequest storingReservationRequest
  @Autowired
  ReservationRequestsViewRepository parkingSpotReservationRequestsViewRepository
  @Autowired
  ReservationRequesterViewRepository reservationRequesterViewRepository
  
  RequesterId requesterId
  
  def setup() {
    def clientId = registerBeneficiary()
    
    requesterId = RequesterId.of(clientId.value)
    
    addParkingSpot(4, ParkingSpotCategory.Gold)
    createTimeSlots()
  }
  
  def "request reservation for parking spot"() {
    given:
      def parkingSpotTimeSlotId = findAnyFullyFreeParkingSpotTimeSlot()
    
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
      def parkingSpotTimeSlotId = findAnyFullyFreeParkingSpotTimeSlot()
    
    when:
      def reservationRequest = storingReservationRequest.makeRequest(
          requesterId, parkingSpotTimeSlotId, SpotUnits.of(4))
    
    then:
      parkingSpotTimeSlotHasNoSpaceLeft(parkingSpotTimeSlotId)
    and:
      requesterContainsReservationRequest(requesterId, reservationRequest.get().reservationRequestId)
  }
  
  private ReservationRequestsTimeSlotId findAnyFullyFreeParkingSpotTimeSlot() {
    return ReservationRequestsTimeSlotId.of(
        parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
            .find { it.spaceLeft() == it.capacity() }
            .timeSlotId())
  }
  
  private void requesterContainsReservationRequest(RequesterId requesterId, ReservationRequestId reservationRequestId) {
    reservationRequesterViewRepository.queryForAllReservationRequesters()
        .find { it -> it.requesterId() == requesterId.value }
        .with {
          assert it.reservationRequests()
              .any { it.reservationRequestId() == reservationRequestId.value }
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
