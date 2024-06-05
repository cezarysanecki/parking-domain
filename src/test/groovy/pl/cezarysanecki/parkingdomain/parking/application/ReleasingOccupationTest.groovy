package pl.cezarysanecki.parkingdomain.parking.application


import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationEvent.OccupationReleased

class ReleasingOccupationTest extends Specification {
  
  OccupationRepository occupationRepository = Mock()
  
  @Subject
  ReleasingOccupation releasingParkingSpot = new ReleasingOccupation(
      occupationRepository)
  
  def "should beneficiary parking spot occupation be released if there is such"() {
    given:
      def beneficiaryId = BeneficiaryId.newOne()
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def occupation = Occupation.newOne(beneficiaryId, parkingSpotId, SpotUnits.of(4))
      occupationRepository.findBy(occupation.occupationId) >> Option.of(occupation)
    
    when:
      def result = releasingParkingSpot.release(occupation.occupationId)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.beneficiaryId == beneficiaryId
        assert it.occupationId == occupation.occupationId
        assert it.spotUnits == spotUnits
      }
    and:
      1 * occupationRepository.publish(_ as OccupationReleased)
  }
  
  def "fail to release parking spot occupation if there no such"() {
    given:
      def beneficiaryId = BeneficiaryId.newOne()
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def occupation = Occupation.newOne(beneficiaryId, parkingSpotId, SpotUnits.of(4))
      occupationRepository.findBy(occupation.occupationId) >> Option.none()
    
    when:
      def result = releasingParkingSpot.release(occupation.occupationId)
    
    then:
      result.isFailure()
  }
  
}
