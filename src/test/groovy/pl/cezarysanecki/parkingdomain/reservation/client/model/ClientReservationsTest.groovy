package pl.cezarysanecki.parkingdomain.reservation.client.model


import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import spock.lang.Specification

class ClientReservationsTest extends Specification {
  
  ClientId clientId = ClientId.of(UUID.randomUUID());
  
  def "allow to reserve whole parking spot when client does not have any active reservation"() {
    given:
      def clientReservations = new ClientReservations(clientId, Set.of())
    and:
      def parkingSpotId = ParkingSpotId.newOne()
    
    when:
      def result = clientReservations.createRequest(parkingSpotId)
    
    then:
      result.isRight()
      result.get().with {
        it.clientId == clientId
        it.parkingSpotId == parkingSpotId
      }
  }
  
}
