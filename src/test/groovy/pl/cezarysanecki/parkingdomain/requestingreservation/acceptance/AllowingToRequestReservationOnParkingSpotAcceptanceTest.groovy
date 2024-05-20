package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.acceptance.AbstractParkingAcceptanceTest
import pl.cezarysanecki.parkingdomain.requestingreservation.application.StoringReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits

class AllowingToRequestReservationOnParkingSpotAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  StoringReservationRequest storingReservationRequest
  @Autowired
  ParkingSpotReservationRequestsViewRepository parkingSpotReservationRequestsViewRepository
  @Autowired
  ReservationRequesterViewRepository reservationRequesterViewRepository
  
  ParkingSpotTimeSlotId parkingSpotTimeSlotId
  ReservationRequesterId requesterId
  
  def setup() {
    def clientId = registerBeneficiary()
    
    parkingSpotTimeSlotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    requesterId = ReservationRequesterId.of(clientId.value)
  }
  
  def "request reservation for parking spot"() {
    when:
      def reservationRequest = storingReservationRequest.storeRequest(
          requesterId, parkingSpotTimeSlotId, SpotUnits.of(2))
    
    then:
      parkingSpotHasSpaceLeft(parkingSpotTimeSlotId, 2)
    and:
      requesterContainsReservationRequest(requesterId, reservationRequest.get().reservationRequestId)
  }
  
  def "request reservation for whole parking spot"() {
    when:
      def reservationRequest = storingReservationRequest.storeRequest(
          requesterId, parkingSpotTimeSlotId, SpotUnits.of(4))
    
    then:
      parkingSpotHasNoSpaceLeft(parkingSpotTimeSlotId)
    and:
      requesterContainsReservationRequest(requesterId, reservationRequest.get().reservationRequestId)
  }
  
  private void parkingSpotHasSpaceLeft(ParkingSpotTimeSlotId parkingSpotTimeSlotId, Integer spaceLeft) {
    parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
    
    
    .find {
      it.capacitiesView().an
    }
        .find { it -> it.capacitiesView().find(a -> a.parkingSpotTimeSlotId() == parkingSpotTimeSlotId.value) }
        .with {
          assert it.spaceLeft() == spaceLeft
        }
  }
  
  private void requesterContainsReservationRequest(ReservationRequesterId requesterId, ReservationRequestId reservationRequestId) {
    reservationRequesterViewRepository.queryForAllReservationRequesters()
        .find { it -> it.reservationRequesterId() == requesterId.value }
        .with {
          assert it.reservationRequests().contains(reservationRequestId.value)
        }
  }
  
  private void parkingSpotHasNoSpaceLeft(ParkingSpotId parkingSpotId) {
    parkingSpotReservationRequestsViewRepository.queryForAllAvailableParkingSpots()
        .each { it -> it.parkingSpotId() != parkingSpotId.value }
  }
  
}
