package pl.cezarysanecki.parkingdomain.parking.model.beneficiary

import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.beneficiaryWithOccupation

class RemovingBeneficiaryOccupationTest extends Specification {
  
  def "remove occupation from beneficiary"() {
    given:
      def occupationId = OccupationId.newOne()
      def beneficiary = beneficiaryWithOccupation(occupationId)
      def occupation = new Occupation(beneficiary.beneficiaryId, occupationId, SpotUnits.of(4))
    
    when:
      def result = beneficiary.remove(occupation)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.occupationId == occupation.occupationId
        assert it.spotUnits == occupation.spotUnits
      }
  }
  
}
