package pl.cezarysanecki.parkingdomain.requestingreservation.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static RequestingReservationForWholeParkingSpot.Command
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithReservationRequest
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithNoReservationRequests

class RequestingReservationForPartOfParkingSpotTest extends Specification {
  
  ClientReservationRequestsRepository clientReservationsRepository = Mock()
  
  @Subject
  RequestingReservationForWholeParkingSpot reservingWholeParkingSpot = new RequestingReservationForWholeParkingSpot(clientReservationsRepository)
  
  def "allow to request client reservation for part of parking spot"() {
    given:
      def clientReservationRequests = clientWithNoReservationRequests()
    and:
      clientReservationsRepository.findBy(clientReservationRequests.clientId) >> Option.of(clientReservationRequests)
    
    when:
      def result = reservingWholeParkingSpot.requestReservation(
          new Command(clientReservationRequests.clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "allow to request client reservation for part of parking spot even when client is not present"() {
    given:
      def clientId = ClientId.newOne()
    and:
      clientReservationsRepository.findBy(clientId) >> Option.none()
    
    when:
      def result = reservingWholeParkingSpot.requestReservation(
          new Command(clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject requesting client reservation for part of parking spot when client has reached limit"() {
    given:
      def clientReservationRequests = clientWithReservationRequest(ReservationId.newOne())
    and:
      clientReservationsRepository.findBy(clientReservationRequests.clientId) >> Option.of(clientReservationRequests)
    
    when:
      def result = reservingWholeParkingSpot.requestReservation(
          new Command(clientReservationRequests.clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
}
