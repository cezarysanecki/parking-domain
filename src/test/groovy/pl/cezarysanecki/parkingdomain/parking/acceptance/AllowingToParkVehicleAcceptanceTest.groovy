package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.OccupationId
import pl.cezarysanecki.parkingdomain.parking.web.BeneficiaryViewRepository
import pl.cezarysanecki.parkingdomain.parking.web.ParkingSpotViewRepository
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

class AllowingToParkVehicleAcceptanceTest extends AbstractParkingAcceptanceTest {
  
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
  
  def "allow to park on parking spot if there is enough space"() {
    when:
      def firstOccupation = occupyingParkingSpot.occupy(
          beneficiaryId, parkingSpotId, SpotUnits.of(2))
    and:
      def secondOccupation = occupyingParkingSpot.occupy(
          beneficiaryId, parkingSpotId, SpotUnits.of(1))
    and:
      def thirdOccupation = occupyingParkingSpot.occupy(
          beneficiaryId, parkingSpotId, SpotUnits.of(1))
    
    then:
      parkingSpotHasNoSpaceLeft(parkingSpotId)
    and:
      beneficiaryContainsAllOccupations(beneficiaryId,
          [firstOccupation.get().occupationId, secondOccupation.get().occupationId, thirdOccupation.get().occupationId])
  }
  
  private void beneficiaryContainsAllOccupations(BeneficiaryId beneficiaryId, List<OccupationId> occupations) {
    beneficiaryViewRepository.queryForAllBeneficiaries()
        .find { it.beneficiaryId() == beneficiaryId.value }
        .with {
          assert it.occupations().collect { OccupationId.of(it) }.every { occupations.contains(it) }
        }
  }
  
  private void parkingSpotHasNoSpaceLeft(ParkingSpotId parkingSpotId) {
    parkingSpotViewRepository.queryForAllAvailableParkingSpots()
        .each { it -> it.parkingSpotId() != parkingSpotId.value }
  }
  
}
