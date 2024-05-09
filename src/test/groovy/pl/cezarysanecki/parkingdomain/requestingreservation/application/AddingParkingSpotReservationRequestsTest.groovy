package pl.cezarysanecki.parkingdomain.requestingreservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId

import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.emptyBeneficiary
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithCapacity

class AddingParkingSpotReservationRequestsTest extends Specification {
  
  EventPublisher eventPublisher = Mock()
  ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository = Mock()
  ReservationRequesterRepository reservationRequesterRepository = Mock()
  
  @Subject
  StoringReservationRequest storingReservationRequest = new StoringReservationRequest(
      eventPublisher,
      reservationRequesterRepository,
      parkingSpotReservationRequestsRepository)
  
  def "create parking spot requests when parking spot is created"() {
    given:
      def parkingSpot = emptyParkingSpotWithCapacity(4)
      parkingSpotRepository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    and:
      def beneficiary = emptyBeneficiary()
      beneficiaryRepository.findBy(beneficiary.beneficiaryId) >> Option.of(beneficiary)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = occupyingParkingSpot.occupy(
          beneficiary.beneficiaryId, parkingSpot.parkingSpotId, spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * beneficiaryRepository.save(beneficiary)
      1 * parkingSpotRepository.save(parkingSpot)
    and:
      1 * eventPublisher.publish(new ParkingSpotEvents.ParkingSpotOccupied(parkingSpot.parkingSpotId, result.get()))
  }
  
}
