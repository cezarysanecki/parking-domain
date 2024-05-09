package pl.cezarysanecki.parkingdomain.parking.model.beneficiary

import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Occupation
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.emptyBeneficiary

class AppendingBeneficiaryOccupationTest extends Specification {
  
  def "allow to append occupation to beneficiary"() {
    given:
      def beneficiary = emptyBeneficiary()
      def spotUnits = SpotUnits.of(4)
      def occupation = Occupation.newOne(beneficiary.beneficiaryId, spotUnits)
    
    when:
      def result = beneficiary.append(occupation)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.occupationId == occupation.occupationId
        assert it.spotUnits == occupation.spotUnits
      }
  }
  
}
