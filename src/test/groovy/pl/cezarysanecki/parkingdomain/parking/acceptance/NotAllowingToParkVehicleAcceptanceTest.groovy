package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.web.BeneficiaryViewRepository
import pl.cezarysanecki.parkingdomain.parking.web.ParkingSpotViewRepository
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class NotAllowingToParkVehicleAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  OccupyingParkingSpot occupyingParkingSpot
  @Autowired
  ParkingSpotViewRepository parkingSpotViewRepository
  @Autowired
  BeneficiaryViewRepository beneficiaryViewRepository
  
  ParkingSpotId parkingSpotId
  
  def setup() {
    parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
  }
  
  def "reject to park on parking spot if there is not enough space"() {
    given:
      def parkingSpotId = addParkingSpot(4, ParkingSpotCategory.Gold)
    and:
      def firstBeneficiaryId = BeneficiaryId.of(registerBeneficiary().value)
    and:
      def secondBeneficiaryId = BeneficiaryId.of(registerBeneficiary().value)
    
    when:
      occupyingParkingSpot.occupy(BeneficiaryId.of(firstBeneficiaryId.value), parkingSpotId, SpotUnits.of(4))
    and:
      occupyingParkingSpot.occupy(BeneficiaryId.of(secondBeneficiaryId.value), parkingSpotId, SpotUnits.of(4))
    
    then:
      parkingSpotHasNoSpaceLeft(parkingSpotId)
    and:
      beneficiaryDoesNotHaveAnyOccupations(secondBeneficiaryId)
  }
  
  private void parkingSpotHasNoSpaceLeft(ParkingSpotId parkingSpotId) {
    parkingSpotViewRepository.queryForAllAvailableParkingSpots()
        .each { it -> it.parkingSpotId() != parkingSpotId.value }
  }
  
  private void beneficiaryDoesNotHaveAnyOccupations(BeneficiaryId beneficiaryId) {
    beneficiaryViewRepository.queryForAllBeneficiaries()
        .find { it.beneficiaryId() == beneficiaryId.value }
        .with {
          assert it.occupations().isEmpty()
        }
  }
  
}
