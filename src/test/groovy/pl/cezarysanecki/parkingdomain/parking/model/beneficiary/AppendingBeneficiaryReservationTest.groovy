package pl.cezarysanecki.parkingdomain.parking.model.beneficiary

import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.emptyBeneficiary

class AppendingBeneficiaryReservationTest extends Specification {
  
  def "allow to append reservation to beneficiary"() {
    given:
      def beneficiary = emptyBeneficiary()
      def spotUnits = SpotUnits.of(4)
      def reservation = Reservation.newOne(beneficiary.beneficiaryId, spotUnits)
    
    when:
      def result = beneficiary.append(reservation)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.reservationId == reservation.reservationId
        assert it.spotUnits == reservation.spotUnits
      }
  }
  
}
