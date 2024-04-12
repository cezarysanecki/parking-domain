package pl.cezarysanecki.parkingdomain.reserving.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForWholeParkingSpotSubmitted
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.WholeParkingSpotReserved
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsFixture.fullyReservedParkingSpotBy
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsFixture.noParkingSpotReservations

class ReservingWholeParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  ReservationId reservationId = ReservationId.newOne()
  
  ParkingSpotReservationsRepository parkingSpotReservationsRepository = Mock()
  
  @Subject
  ClientReservationsEventHandler clientReservationsEventHandler = new ClientReservationsEventHandler(parkingSpotReservationsRepository)
  
  def "reserve whole parking spot when client made a request for it"() {
    given:
      def parkingSpotReservations = noParkingSpotReservations()
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotReservations.parkingSpotId) >> Option.of(parkingSpotReservations)
    
    when:
      clientReservationsEventHandler.handle(new ReservationForWholeParkingSpotSubmitted(
          clientId, reservationId, parkingSpotReservations.parkingSpotId))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotReservations.parkingSpotId
            && it.reservationId == reservationId
      } as WholeParkingSpotReserved)
  }
  
  def "reject reserving whole parking spot when client made a request for it when there is reservation already"() {
    given:
      def parkingSpotReservations = fullyReservedParkingSpotBy(ReservationId.newOne())
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotReservations.parkingSpotId) >> Option.of(parkingSpotReservations)
    
    when:
      clientReservationsEventHandler.handle(new ReservationForWholeParkingSpotSubmitted(
          clientId, reservationId, parkingSpotReservations.parkingSpotId))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotReservations.parkingSpotId
            && it.reservationId == reservationId
      } as ParkingSpotReservationFailed)
  }
  
  def "reject reserving whole parking spot when client made a request for it when parking spot reservations does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      clientReservationsEventHandler.handle(new ReservationForWholeParkingSpotSubmitted(
          clientId, reservationId, parkingSpotId))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.reservationId == reservationId
      } as ParkingSpotReservationFailed)
  }
  
}