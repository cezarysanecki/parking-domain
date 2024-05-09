package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CancellingReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.application.StoringReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class CancellingReservationRequestForParkingSpotAcceptanceTest extends AbstractRequestingAcceptanceTest {
  
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
    def clientId = registerClient("123123123")
    
    parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    requesterId = ReservationRequesterId.of(clientId.value)
  }
  
  def "cancel parking spot reservation request"() {
    given:
      def reservationRequest = storingReservationRequest.storeRequest(
          requesterId, parkingSpotId, SpotUnits.of(2))
    
    when:
      cancellingReservationRequest.cancelRequest(reservationRequest.get().getReservationRequestId())
    
    then:
      parkingSpotIsEmpty(parkingSpotId)
    and:
      requesterDoesNotContainAnyReservationRequest(requesterId)
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
