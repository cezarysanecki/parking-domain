package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot

import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import static ParkingSpotReservationRequestsFixture.parkingSpotWithoutReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ParkingSpotReservationRequestsFixture.parkingSpotFullyRequested

class StoringRequestForParkingSpotTest extends Specification {
  
  def "allow to store reservation request for parking spot"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
    and:
      def requesterId = ReservationRequesterId.newOne()
    and:
      def spotUnits = SpotUnits.of(2)
    
    when:
      def result = parkingSpotReservationRequests.append(requesterId, spotUnits)
    
    then:
      result.isSuccess()
      result.get().with {
        assert it.reservationRequest().reservationRequesterId == requesterId
        assert it.reservationRequest().reservationRequesterId == requesterId
        assert it.reservationRequest().spotUnits == spotUnits
      }
  }
  
  def "reject storing reservation request for parking spot when there is not enough space"() {
    given:
      def parkingSpotRequests = parkingSpotFullyRequested()
    and:
      def requesterId = ReservationRequesterId.newOne()
    
    when:
      def result = parkingSpotRequests.append(requesterId, SpotUnits.of(1))
    
    then:
      result.isFailure()
  }
  
}