package pl.cezarysanecki.parkingdomain.requesting.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.CancellingReservationRequest
import pl.cezarysanecki.parkingdomain.requesting.MakingReservationRequest
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsTimeSlotId
import pl.cezarysanecki.parkingdomain.web.ReservationRequesterViewRepository
import pl.cezarysanecki.parkingdomain.web.ReservationRequestsViewRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits

class RemovingReservationRequestForParkingSpotAcceptanceTest extends AbstractRequestingAcceptanceTest {
  
  @Autowired
  MakingReservationRequest storingReservationRequest
  @Autowired
  CancellingReservationRequest cancellingReservationRequest
  @Autowired
  ReservationRequestsViewRepository parkingSpotReservationRequestsViewRepository
  @Autowired
  ReservationRequesterViewRepository reservationRequesterViewRepository
  
  ParkingSpotId parkingSpotId
  RequesterId requesterId
  
  def setup() {
    parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    requesterId = registerLowLimitRequester()
    
    createTimeSlots()
  }
  
  def "cancel parking spot reservation request"() {
    given:
      def parkingSpotTimeSlotId = findAnyFullyFreeParkingSpotTimeSlot(parkingSpotId)
    and:
      def reservationRequest = storingReservationRequest.makeRequest(
          requesterId, parkingSpotTimeSlotId, SpotUnits.of(2))
    
    when:
      cancellingReservationRequest.cancelRequest(reservationRequest.get().getReservationRequestId())
    
    then:
      parkingSpotIsEmpty(parkingSpotId)
    and:
      requesterDoesNotContainAnyReservationRequest(requesterId)
  }
  
  private ReservationRequestsTimeSlotId findAnyFullyFreeParkingSpotTimeSlot(ParkingSpotId parkingSpotId) {
    return ReservationRequestsTimeSlotId.of(
        parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
            .find { it.parkingSpotId() == parkingSpotId.value && it.spaceLeft() == it.capacity() }
            .timeSlotId())
  }
  
  private void parkingSpotIsEmpty(ParkingSpotId parkingSpotId) {
    parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
        .find { it -> it.parkingSpotId() == parkingSpotId.value }
        .with {
          assert it.spaceLeft() == it.capacity()
        }
  }
  
  private void requesterDoesNotContainAnyReservationRequest(RequesterId requesterId) {
    reservationRequesterViewRepository.queryForAllReservationRequesters()
        .find { it -> it.requesterId() == requesterId.value }
        .with {
          assert it.reservationRequests().empty
        }
  }
  
}
