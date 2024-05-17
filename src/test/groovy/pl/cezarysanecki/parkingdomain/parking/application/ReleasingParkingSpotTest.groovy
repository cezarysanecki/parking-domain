package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.collection.HashSet
import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Occupation
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.OccupationId
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents.ParkingSpotReleased
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotFixture.occupiedFullyBy

class ReleasingParkingSpotTest extends Specification {
  
  EventPublisher eventPublisher = Mock()
  BeneficiaryRepository beneficiaryRepository = Mock()
  ParkingSpotRepository parkingSpotRepository = Mock()
  
  @Subject
  ReleasingParkingSpot releasingParkingSpot = new ReleasingParkingSpot(
      eventPublisher,
      beneficiaryRepository,
      parkingSpotRepository)
  
  def "should beneficiary parking spot occupation be released if there is such"() {
    given:
      def beneficiaryId = BeneficiaryId.newOne()
      def occupation = Occupation.newOne(beneficiaryId, SpotUnits.of(4))
    and:
      def beneficiary = new Beneficiary(beneficiaryId, HashSet.empty(), HashSet.of(occupation.occupationId), Version.zero())
      beneficiaryRepository.findBy(occupation.occupationId) >> Option.of(beneficiary)
    and:
      def parkingSpot = occupiedFullyBy(occupation)
      parkingSpotRepository.findBy(occupation.occupationId) >> Option.of(parkingSpot)
    
    when:
      def result = releasingParkingSpot.release(occupation.occupationId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiary.beneficiaryId
        assert it.occupationId == occupation.occupationId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * beneficiaryRepository.save(beneficiary)
      1 * parkingSpotRepository.save(parkingSpot)
    and:
      1 * eventPublisher.publish(_ as ParkingSpotReleased)
  }
  
  def "fail to release parking spot occupation if there no such"() {
    given:
      def beneficiaryId = BeneficiaryId.newOne()
      def occupation = Occupation.newOne(beneficiaryId, SpotUnits.of(4))
    and:
      def beneficiary = new Beneficiary(beneficiaryId, HashSet.empty(), HashSet.of(occupation.occupationId), Version.zero())
      beneficiaryRepository.findBy(_ as OccupationId) >> Option.of(beneficiary)
    and:
      def parkingSpot = occupiedFullyBy(occupation)
      parkingSpotRepository.findBy(_ as OccupationId) >> Option.of(parkingSpot)
    
    when:
      def result = releasingParkingSpot.release(OccupationId.newOne())
    
    then:
      result.isFailure()
  }
  
}
