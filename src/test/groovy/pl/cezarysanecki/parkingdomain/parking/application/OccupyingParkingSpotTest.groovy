package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Reservation
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryFixture.emptyBeneficiary
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents.ParkingSpotOccupied
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithCapacity
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.emptyParkingSpotWithReservation

class OccupyingParkingSpotTest extends Specification {
  
  EventPublisher eventPublisher = Mock()
  BeneficiaryRepository beneficiaryRepository = Mock()
  ParkingSpotRepository parkingSpotRepository = Mock()
  
  @Subject
  OccupyingParkingSpot occupyingParkingSpot = new OccupyingParkingSpot(
      eventPublisher,
      beneficiaryRepository,
      parkingSpotRepository)
  
  def "should occupy parking spot by beneficiary"() {
    given:
      def parkingSpot = emptyParkingSpotWithCapacity(4)
      parkingSpotRepository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    and:
      def beneficiary = emptyBeneficiary()
      beneficiaryRepository.findBy(beneficiary.beneficiaryId) >> Option.of(beneficiary)
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = occupyingParkingSpot.occupy(
          beneficiary.beneficiaryId, parkingSpot.parkingSpotId, spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * beneficiaryRepository.save(beneficiary)
      1 * parkingSpotRepository.save(parkingSpot)
    and:
      1 * eventPublisher.publish(_ as ParkingSpotOccupied)
  }
  
  def "should occupy whole parking spot by beneficiary"() {
    given:
      def capacity = 4
    and:
      def parkingSpot = emptyParkingSpotWithCapacity(capacity)
      parkingSpotRepository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    and:
      def beneficiary = emptyBeneficiary()
      beneficiaryRepository.findBy(beneficiary.beneficiaryId) >> Option.of(beneficiary)
    
    when:
      def result = occupyingParkingSpot.occupyWhole(beneficiary.beneficiaryId, parkingSpot.parkingSpotId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.spotUnits == SpotUnits.of(capacity)
      }
    and:
      1 * beneficiaryRepository.save(beneficiary)
      1 * parkingSpotRepository.save(parkingSpot)
    and:
      1 * eventPublisher.publish(_ as ParkingSpotOccupied)
  }
  
  def "should occupy parking spot by beneficiary with reservation"() {
    given:
      def beneficiary = emptyBeneficiary()
      beneficiaryRepository.findBy(beneficiary.beneficiaryId) >> Option.of(beneficiary)
    and:
      def spotUnits = SpotUnits.of(2)
      def reservation = Reservation.newOne(beneficiary.beneficiaryId, spotUnits)
    and:
      def parkingSpot = emptyParkingSpotWithReservation(reservation)
      parkingSpotRepository.findBy(reservation.reservationId) >> Option.of(parkingSpot)
    
    when:
      def result = occupyingParkingSpot.occupy(reservation.reservationId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * beneficiaryRepository.save(beneficiary)
      1 * parkingSpotRepository.save(parkingSpot)
    and:
      1 * eventPublisher.publish(_ as ParkingSpotOccupied)
  }
  
  def "fail to occupy parking spot by beneficiary when parking spot does not have enough space"() {
    given:
      def capacity = 4
    and:
      def parkingSpot = emptyParkingSpotWithCapacity(capacity)
      parkingSpotRepository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    and:
      def beneficiary = emptyBeneficiary()
      beneficiaryRepository.findBy(beneficiary.beneficiaryId) >> Option.of(beneficiary)
    
    when:
      def result = occupyingParkingSpot.occupy(
          beneficiary.beneficiaryId, parkingSpot.parkingSpotId, SpotUnits.of(capacity + 1))
    
    then:
      result.isFailure()
  }
  
}
