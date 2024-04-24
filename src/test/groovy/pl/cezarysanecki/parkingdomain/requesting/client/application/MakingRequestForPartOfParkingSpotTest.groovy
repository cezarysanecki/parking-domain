package pl.cezarysanecki.parkingdomain.requesting.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static MakingRequestForWholeParkingSpot.Command
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithRequest
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithNoRequests

class MakingRequestForPartOfParkingSpotTest extends Specification {
  
  ClientRequestsRepository clientReservationsRepository = Mock()
  
  @Subject
  MakingRequestForWholeParkingSpot reservingWholeParkingSpot = new MakingRequestForWholeParkingSpot(clientReservationsRepository)
  
  def "allow to request client reservation for part of parking spot"() {
    given:
      def clientReservationRequests = clientWithNoRequests()
    and:
      clientReservationsRepository.findBy(clientReservationRequests.clientId) >> Option.of(clientReservationRequests)
    
    when:
      def result = reservingWholeParkingSpot.makeRequest(
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
      def result = reservingWholeParkingSpot.makeRequest(
          new Command(clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject requesting client reservation for part of parking spot when client has reached limit"() {
    given:
      def clientReservationRequests = clientWithRequest(ReservationId.newOne())
    and:
      clientReservationsRepository.findBy(clientReservationRequests.clientId) >> Option.of(clientReservationRequests)
    
    when:
      def result = reservingWholeParkingSpot.makeRequest(
          new Command(clientReservationRequests.clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
}
