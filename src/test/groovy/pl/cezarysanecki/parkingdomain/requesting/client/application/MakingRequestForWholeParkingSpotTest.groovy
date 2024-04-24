package pl.cezarysanecki.parkingdomain.requesting.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static MakingRequestForPartOfParkingSpot.Command
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithNoRequests
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithRequest

class MakingRequestForWholeParkingSpotTest extends Specification {
  
  ClientRequestsRepository clientReservationRequestsRepository = Mock()
  
  @Subject
  MakingRequestForPartOfParkingSpot requestingReservationForPartOfParkingSpot = new MakingRequestForPartOfParkingSpot(clientReservationRequestsRepository)
  
  def "allow to request client reservation for whole parking spot"() {
    given:
      def clientReservationRequests = clientWithNoRequests()
    and:
      clientReservationRequestsRepository.findBy(clientReservationRequests.clientId) >> Option.of(clientReservationRequests)
    
    when:
      def result = requestingReservationForPartOfParkingSpot.makeRequest(
          new Command(clientReservationRequests.clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "allow to request client reservation for whole parking spot even when client is not present"() {
    given:
      def clientId = ClientId.newOne()
    and:
      clientReservationRequestsRepository.findBy(clientId) >> Option.none()
    
    when:
      def result = requestingReservationForPartOfParkingSpot.makeRequest(
          new Command(clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject requesting client reservation for whole parking spot when client has reached limit"() {
    given:
      def clientReservations = clientWithRequest(ReservationId.newOne())
    and:
      clientReservationRequestsRepository.findBy(clientReservations.clientId) >> Option.of(clientReservations)
    
    when:
      def result = requestingReservationForPartOfParkingSpot.makeRequest(
          new Command(clientReservations.clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
}
