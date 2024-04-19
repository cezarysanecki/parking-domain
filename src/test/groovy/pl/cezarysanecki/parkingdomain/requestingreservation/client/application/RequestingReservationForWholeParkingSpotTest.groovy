package pl.cezarysanecki.parkingdomain.requestingreservation.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static RequestingReservationForPartOfParkingSpot.Command
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithNoReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithReservationRequest

class RequestingReservationForWholeParkingSpotTest extends Specification {
  
  ClientReservationRequestsRepository clientReservationRequestsRepository = Mock()
  
  @Subject
  RequestingReservationForPartOfParkingSpot requestingReservationForPartOfParkingSpot = new RequestingReservationForPartOfParkingSpot(clientReservationRequestsRepository)
  
  def "allow to request client reservation for whole parking spot"() {
    given:
      def clientReservationRequests = clientWithNoReservationRequests()
    and:
      clientReservationRequestsRepository.findBy(clientReservationRequests.clientId) >> Option.of(clientReservationRequests)
    
    when:
      def result = requestingReservationForPartOfParkingSpot.requestReservation(
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
      def result = requestingReservationForPartOfParkingSpot.requestReservation(
          new Command(clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject requesting client reservation for whole parking spot when client has reached limit"() {
    given:
      def clientReservations = clientWithReservationRequest(ReservationId.newOne())
    and:
      clientReservationRequestsRepository.findBy(clientReservations.clientId) >> Option.of(clientReservations)
    
    when:
      def result = requestingReservationForPartOfParkingSpot.requestReservation(
          new Command(clientReservations.clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
}
