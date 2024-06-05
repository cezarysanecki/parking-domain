package pl.cezarysanecki.parkingdomain.parking.model.parkingspot

import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static ParkingSpotFixture.emptyParkingSpotWithCapacity
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.*

class OccupyingParkingSpotTest extends Specification {
  
  def "allow to occupy parking spot by beneficiary"() {
    given:
      def beneficiary = BeneficiaryId.newOne()
      def spotUnits = SpotUnits.of(2)
    
    and:
      def emptyParkingSpot = emptyParkingSpotWithCapacity(4)
    
    when:
      def result = emptyParkingSpot.occupy(beneficiary, spotUnits)
    
    then:
      result.isRight()
      result.get().with {
        assert it.occupied().occupation().parkingSpotId == emptyParkingSpot.parkingSpotId
        assert it.occupied().occupation().beneficiaryId == beneficiary
        assert it.occupied().occupation().spotUnits == spotUnits
        
        assert it.reservationFulfilled().empty
      }
  }
  
  def "allow to occupy whole parking spot by beneficiary"() {
    given:
      def beneficiary = BeneficiaryId.newOne()
      def capacity = 4
    
    and:
      def emptyParkingSpot = emptyParkingSpotWithCapacity(capacity)
    
    when:
      def result = emptyParkingSpot.occupyWhole(beneficiary)
    
    then:
      result.isRight()
      result.get().with {
        assert it.occupied().occupation().parkingSpotId == emptyParkingSpot.parkingSpotId
        assert it.occupied().occupation().beneficiaryId == beneficiary
        assert it.occupied().occupation().spotUnits == SpotUnits.of(capacity)
        
        assert it.reservationFulfilled().empty
      }
  }
  
  def "allow to occupy parking spot using reservation"() {
    given:
      def beneficiary = BeneficiaryId.newOne()
      def spotUnits = SpotUnits.of(4)
    and:
      def emptyParkingSpot = emptyParkingSpotWithReservationFor(beneficiary, spotUnits)
      def reservation = emptyParkingSpot.reservations.values().first()
    
    when:
      def result = emptyParkingSpot.occupyUsing(reservation.reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.occupied().occupation().parkingSpotId == emptyParkingSpot.parkingSpotId
        assert it.occupied().occupation().beneficiaryId == beneficiary
        assert it.occupied().occupation().spotUnits == spotUnits
        
        assert it.reservationFulfilled().get().reservation() == reservation
      }
  }
  
  def "reject to occupy parking spot by beneficiary when it does not have enough space"() {
    given:
      def beneficiary = BeneficiaryId.newOne()
      def spotUnits = SpotUnits.of(4)
    
    and:
      def fullyOccupiedParkingSpot = fullyOccupiedParkingSpot()
    
    when:
      def result = fullyOccupiedParkingSpot.occupy(beneficiary, spotUnits)
    
    then:
      result.isLeft()
  }
  
  def "reject to occupy whole parking spot by beneficiary when it is already occupied"() {
    given:
      def beneficiary = BeneficiaryId.newOne()
    
    and:
      def fullyOccupiedParkingSpot = occupiedWithLeftSpace(1)
    
    when:
      def result = fullyOccupiedParkingSpot.occupyWhole(beneficiary)
    
    then:
      result.isLeft()
  }
  
  def "reject to occupy parking spot by beneficiary with reservation when there is no such reservation"() {
    given:
      def fullyOccupiedParkingSpot = emptyParkingSpotWithReservationFor(BeneficiaryId.newOne(), SpotUnits.of(4))
    
    when:
      def result = fullyOccupiedParkingSpot.occupyUsing(ReservationId.newOne())
    
    then:
      result.isLeft()
  }
  
}
