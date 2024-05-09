package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId
import pl.cezarysanecki.parkingdomain.parking.web.BeneficiaryViewRepository
import pl.cezarysanecki.parkingdomain.parking.web.ParkingSpotViewRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class AllowingToParkOnReservedParkingSpotAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  OccupyingParkingSpot occupyingParkingSpot
  @Autowired
  ParkingSpotViewRepository parkingSpotViewRepository
  @Autowired
  BeneficiaryViewRepository beneficiaryViewRepository
  
  ParkingSpotId parkingSpotId
  BeneficiaryId beneficiaryId
  
  def setup() {
    def clientId = registerClient("123123123")
    
    parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    beneficiaryId = BeneficiaryId.of(clientId.value)
  }
  
  def "allow to park on reserved parking spot"() {
    given:
      def reservationId = reserveParkingSpot(
          parkingSpotId, ReservationRequesterId.of(beneficiaryId.value), SpotUnits.of(2))
    
    when:
      def occupation = occupyingParkingSpot.occupy(reservationId)
    
    then:
      parkingSpotHasSpaceLeft(parkingSpotId, 2)
    and:
      beneficiaryContainsOccupation(beneficiaryId, occupation)
  }
  
  def "reject to park on reserved parking spot if this is not reserved for this beneficiary"() {
    when:
      occupyingParkingSpot.occupy(ReservationId.newOne())
    
    then:
      parkingSpotHasSpaceLeft(parkingSpotId, 4)
    and:
      beneficiaryDoesNotHaveAnyOccupations(beneficiaryId)
  }
  
  private void beneficiaryContainsOccupation(BeneficiaryId beneficiaryId, occupation) {
    beneficiaryViewRepository.queryForAllBeneficiaries()
        .find { it.beneficiaryId() == beneficiaryId.value }
        .with {
          assert it.occupations().contains(occupation.get().occupationId.value)
        }
  }
  
  private void beneficiaryDoesNotHaveAnyOccupations(BeneficiaryId beneficiaryId) {
    beneficiaryViewRepository.queryForAllBeneficiaries()
        .find { it.beneficiaryId() == beneficiaryId.value }
        .with {
          assert it.occupations().isEmpty()
        }
  }
  
  private void parkingSpotHasSpaceLeft(ParkingSpotId parkingSpotId, Integer spaceLeft) {
    parkingSpotViewRepository.queryForAllAvailableParkingSpots()
        .find { it -> it.parkingSpotId() == parkingSpotId.value }
        .with {
          assert it.spaceLeft() == spaceLeft
        }
  }
  
}
