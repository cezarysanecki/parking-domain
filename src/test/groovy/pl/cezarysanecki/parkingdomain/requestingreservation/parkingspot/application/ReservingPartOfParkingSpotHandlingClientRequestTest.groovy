package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationEvent.PartOfParkingSpotReserved
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.fullyReservedParkingSpotBy
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.noParkingSpotReservations

class ReservingPartOfParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  ReservationId reservationId = ReservationId.newOne()
  
  ParkingSpotReservationsRepository parkingSpotReservationsRepository = Mock()
  
  @Subject
  HandlingClientReservationsEventHandler clientReservationsEventHandler = new HandlingClientReservationsEventHandler(parkingSpotReservationsRepository)
  
  def "reserve part of parking spot when client made a request for it"() {
    given:
      def vehicleSize = VehicleSize.of(2)
    and:
      def parkingSpotReservations = noParkingSpotReservations()
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotReservations.parkingSpotId) >> Option.of(parkingSpotReservations)
    
    when:
      clientReservationsEventHandler.handle(new ReservationForPartOfParkingSpotSubmitted(
          clientId, reservationId, parkingSpotReservations.parkingSpotId, vehicleSize))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotReservations.parkingSpotId
            && it.reservationId == reservationId
            && it.vehicleSize == vehicleSize
      } as PartOfParkingSpotReserved)
  }
  
  def "reject reserving part of parking spot when client made a request for it when there is not enough place"() {
    given:
      def parkingSpotReservations = fullyReservedParkingSpotBy(ReservationId.newOne())
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotReservations.parkingSpotId) >> Option.of(parkingSpotReservations)
    
    when:
      clientReservationsEventHandler.handle(new ReservationForPartOfParkingSpotSubmitted(
          clientId, reservationId, parkingSpotReservations.parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotReservations.parkingSpotId
            && it.reservationId == reservationId
      } as ParkingSpotReservationFailed)
  }
  
  def "reject reserving part of parking spot when client made a request for it when parking spot reservations does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      clientReservationsEventHandler.handle(new ReservationForPartOfParkingSpotSubmitted(
          clientId, reservationId, parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.reservationId == reservationId
      } as ParkingSpotReservationFailed)
  }
  
}
