package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option

import pl.cezarysanecki.parkingdomain.parking.ParkingSpotRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.ParkingSpotEvent.ParkingSpotOccupiedEvents
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithReservationFor

class OccupyingReservedParkingSpotTest extends Specification {
  
  ParkingSpotRepository parkingSpotRepository = Mock()
  
  @Subject
  OccupyingReservedParkingSpot occupyingReservedParkingSpot = new OccupyingReservedParkingSpot(
      parkingSpotRepository)
  
  def "should occupy parking spot by beneficiary with reservation"() {
    given:
      def beneficiaryId = BeneficiaryId.newOne()
      def spotUnits = SpotUnits.of(2)
    and:
      def parkingSpot = emptyParkingSpotWithReservationFor(beneficiaryId, spotUnits)
      def reservation = parkingSpot.reservations.values().first()
      parkingSpotRepository.findBy(reservation.reservationId) >> Option.of(parkingSpot)
    
    when:
      def result = occupyingReservedParkingSpot.occupy(reservation.reservationId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiaryId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * parkingSpotRepository.publish(_ as ParkingSpotOccupiedEvents)
  }
  
}
