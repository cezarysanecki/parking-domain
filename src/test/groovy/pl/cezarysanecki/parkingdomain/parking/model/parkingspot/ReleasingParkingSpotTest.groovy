package pl.cezarysanecki.parkingdomain.parking.model.parkingspot

import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static ParkingSpotFixture.occupiedFullyBy

class ReleasingParkingSpotTest extends Specification {
  
  def "allow to release parking spot by beneficiary"() {
    given:
      def beneficiary = BeneficiaryId.newOne()
      def occupation = Occupation.newOne(beneficiary, SpotUnits.of(4))
    and:
      def occupiedParkingSpot = occupiedFullyBy(occupation)
    
    when:
      def result = occupiedParkingSpot.release(occupation.occupationId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary
        assert it.occupationId == occupation.occupationId
        assert it.spotUnits == occupation.spotUnits
      }
  }
  
  def "reject to release parking spot by beneficiary when there is no his occupations"() {
    given:
      def beneficiary = BeneficiaryId.newOne()
      def occupation = Occupation.newOne(beneficiary, SpotUnits.of(4))
    and:
      def occupiedParkingSpot = occupiedFullyBy(occupation)
    
    when:
      def result = occupiedParkingSpot.release(OccupationId.newOne())
    
    then:
      result.isFailure()
  }
  
}
