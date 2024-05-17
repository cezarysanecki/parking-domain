package pl.cezarysanecki.parkingdomain.parking.model.beneficiary

import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Reservation
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.beneficiaryWithReservation

class RemovingBeneficiaryReservationTest extends Specification {
  
  def "remove reservation from beneficiary"() {
    given:
      def reservationId = ReservationId.newOne()
      def beneficiary = beneficiaryWithReservation(reservationId)
      def reservation = new Reservation(beneficiary.beneficiaryId, reservationId, SpotUnits.of(4))
    
    when:
      def result = beneficiary.remove(reservation)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.reservationId == reservation.reservationId
        assert it.spotUnits == reservation.spotUnits
      }
  }
  
}
