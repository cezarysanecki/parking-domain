package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot


import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static TimeSlotReservationRequestsFixture.timeSlotFullyRequested
import static TimeSlotReservationRequestsFixture.timeSlotWithoutRequests

class AppendingRequestForParkingSpotTest extends Specification {
  
  def "allow to append reservation request"() {
    given:
      def parkingSpotReservationRequests = timeSlotWithoutRequests()
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = parkingSpotReservationRequests.append(spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it == spotUnits
      }
  }
  
  def "reject storing reservation request for parking spot when there is not enough space"() {
    given:
      def parkingSpotRequests = timeSlotFullyRequested()
    
    when:
      def result = parkingSpotRequests.append(SpotUnits.of(1))
    
    then:
      result.isFailure()
  }
  
}
