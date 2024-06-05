package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.anyBeneficiary
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.*
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithCapacity

class OccupyingRandomParkingSpotTest extends Specification {
  
  BeneficiaryRepository beneficiaryRepository = Mock()
  ParkingSpotRepository parkingSpotRepository = Mock()
  
  @Subject
  OccupyingRandomParkingSpot occupyingRandomParkingSpot = new OccupyingRandomParkingSpot(
      beneficiaryRepository,
      parkingSpotRepository)
  
  def "should occupy any parking spot by beneficiary"() {
    given:
      def parkingSpotCategory = ParkingSpotCategory.Gold
      def spotUnits = SpotUnits.of(2)
    and:
      def parkingSpot = emptyParkingSpotWithCapacity(4)
      parkingSpotRepository.findAvailableFor(parkingSpotCategory, spotUnits) >> Option.of(parkingSpot)
    and:
      def beneficiary = anyBeneficiary()
      beneficiaryRepository.isPresent(beneficiary) >> true
    and:
    
    when:
      def result = occupyingRandomParkingSpot.occupy(
          beneficiary, parkingSpotCategory, spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary
        assert it.spotUnits == spotUnits
      }
    and:
      1 * parkingSpotRepository.publish(_ as ParkingSpotOccupiedEvents)
  }
  
}
