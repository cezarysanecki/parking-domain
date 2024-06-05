package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.anyBeneficiary
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.ParkingSpotOccupiedEvents
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithCapacity

class OccupyingParkingSpotTest extends Specification {
  
  BeneficiaryRepository beneficiaryRepository = Mock()
  ParkingSpotRepository parkingSpotRepository = Mock()
  
  @Subject
  OccupyingParkingSpot occupyingParkingSpot = new OccupyingParkingSpot(
      beneficiaryRepository,
      parkingSpotRepository)
  
  def "should occupy parking spot by beneficiary"() {
    given:
      def parkingSpot = emptyParkingSpotWithCapacity(4)
      parkingSpotRepository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    and:
      def beneficiary = anyBeneficiary()
      beneficiaryRepository.isPresent(beneficiary) >> true
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = occupyingParkingSpot.occupy(
          beneficiary, parkingSpot.parkingSpotId, spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary
        assert it.spotUnits == spotUnits
      }
    and:
      1 * parkingSpotRepository.publish(_ as ParkingSpotOccupiedEvents)
  }
  
  def "should occupy whole parking spot by beneficiary"() {
    given:
      def capacity = 4
    and:
      def parkingSpot = emptyParkingSpotWithCapacity(capacity)
      parkingSpotRepository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    and:
      def beneficiary = anyBeneficiary()
      beneficiaryRepository.isPresent(beneficiary) >> true
    
    when:
      def result = occupyingParkingSpot.occupyWhole(beneficiary, parkingSpot.parkingSpotId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary
        assert it.spotUnits == SpotUnits.of(capacity)
      }
    and:
      1 * parkingSpotRepository.publish(_ as ParkingSpotOccupiedEvents)
  }
  
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
      def result = occupyingParkingSpot.occupyAvailable(
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
  
  def "fail to occupy parking spot by beneficiary when parking spot does not have enough space"() {
    given:
      def capacity = 4
    and:
      def parkingSpot = emptyParkingSpotWithCapacity(capacity)
      parkingSpotRepository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    and:
      def beneficiary = anyBeneficiary()
      beneficiaryRepository.isPresent(beneficiary) >> true
    
    when:
      def result = occupyingParkingSpot.occupy(
          beneficiary, parkingSpot.parkingSpotId, SpotUnits.of(capacity + 1))
    
    then:
      result.isFailure()
  }
  
}
