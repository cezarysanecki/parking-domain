package pl.cezarysanecki.parkingdomain.parking.acceptance


import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId


import pl.cezarysanecki.parkingdomain.web.BeneficiaryViewRepository
import pl.cezarysanecki.parkingdomain.web.ParkingSpotViewRepository
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits

class AllowingToParkOnReservedParkingSpotAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  OccupyingReservedParkingSpot occupyingReservedParkingSpot
  @Autowired
  ParkingSpotViewRepository parkingSpotViewRepository
  @Autowired
  BeneficiaryViewRepository beneficiaryViewRepository
  
  ParkingSpotId parkingSpotId
  BeneficiaryId beneficiaryId
  
  def setup() {
    parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    beneficiaryId = registerBeneficiary()
  }
  
  def "allow to park on reserved parking spot"() {
    given:
      def reservationId = reserveParkingSpot(
          parkingSpotId, RequesterId.of(beneficiaryId.value), SpotUnits.of(2))
    
    when:
      def occupation = occupyingReservedParkingSpot.occupy(reservationId)
    
    then:
      parkingSpotHasSpaceLeft(parkingSpotId, 2)
    and:
      beneficiaryContainsOccupation(beneficiaryId, occupation)
  }
  
  def "reject to park on reserved parking spot if this is not reserved for this beneficiary"() {
    when:
      def result = occupyingReservedParkingSpot.occupy(ReservationId.newOne())
    
    then:
      result.isFailure()
    and:
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
