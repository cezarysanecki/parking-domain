package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CancellingReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.application.StoringReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits

class RemovingReservationRequestForParkingSpotAcceptanceTest extends AbstractRequestingAcceptanceTest {
  
  @Autowired
  StoringReservationRequest storingReservationRequest
  @Autowired
  CancellingReservationRequest cancellingReservationRequest
  @Autowired
  ParkingSpotReservationRequestsViewRepository parkingSpotReservationRequestsViewRepository
  @Autowired
  ReservationRequesterViewRepository reservationRequesterViewRepository
  
  ParkingSpotId parkingSpotId
  ReservationRequesterId requesterId
  
  def setup() {
    parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    requesterId = registerLowLimitRequester()
    
    createTimeSlots()
  }
  
  def "cancel parking spot reservation request"() {
    given:
      def parkingSpotTimeSlotId = findAnyFullyFreeParkingSpotTimeSlot(parkingSpotId)
    and:
      def reservationRequest = storingReservationRequest.storeRequest(
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
            .parkingSpotTimeSlotId())
  }
  
  private void parkingSpotIsEmpty(ParkingSpotId parkingSpotId) {
    parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
        .find { it -> it.parkingSpotId() == parkingSpotId.value }
        .with {
          assert it.spaceLeft() == it.capacity()
        }
  }
  
  private void requesterDoesNotContainAnyReservationRequest(ReservationRequesterId requesterId) {
    reservationRequesterViewRepository.queryForAllReservationRequesters()
        .find { it -> it.reservationRequesterId() == requesterId.value }
        .with {
          assert it.reservationRequests().empty
        }
  }
  
}
