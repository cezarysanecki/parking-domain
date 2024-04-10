package pl.cezarysanecki.parkingdomain.reserving.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingPartOfParkingSpot.Command
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsFixture.clientReservationsWithReservation
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsFixture.noClientReservations

class ReservingPartOfParkingSpotTest extends Specification {
  
  ClientReservationsRepository clientReservationsRepository = Mock()
  
  @Subject
  ReservingPartOfParkingSpot reservingPartOfParkingSpot = new ReservingPartOfParkingSpot(clientReservationsRepository)
  
  def "allow to request client reservation"() {
    given:
      def clientReservations = noClientReservations()
    and:
      clientReservationsRepository.findBy(clientReservations.clientId) >> Option.of(clientReservations)
    
    when:
      def result = reservingPartOfParkingSpot.requestReservation(
          new Command(clientReservations.clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "allow to request client reservation even when client is not present"() {
    given:
      def clientId = ClientId.newOne()
    and:
      clientReservationsRepository.findBy(clientId) >> Option.none()
    
    when:
      def result = reservingPartOfParkingSpot.requestReservation(
          new Command(clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject requesting client reservation when client has reached limit"() {
    given:
      def clientReservations = clientReservationsWithReservation(ReservationId.newOne())
    and:
      clientReservationsRepository.findBy(clientReservations.clientId) >> Option.of(clientReservations)
    
    when:
      def result = reservingPartOfParkingSpot.requestReservation(
          new Command(clientReservations.clientId, ParkingSpotId.newOne(), VehicleSize.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
}
