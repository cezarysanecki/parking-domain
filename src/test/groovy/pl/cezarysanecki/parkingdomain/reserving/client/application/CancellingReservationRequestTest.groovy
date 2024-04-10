package pl.cezarysanecki.parkingdomain.reserving.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.reserving.client.application.CancellingReservationRequest.Command
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsFixture.clientReservationsWithReservation
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsFixture.noClientReservations

class CancellingReservationRequestTest extends Specification {
  
  ClientReservationsRepository clientReservationsRepository = Mock()
  
  @Subject
  CancellingReservationRequest cancellingReservationRequest = new CancellingReservationRequest(clientReservationsRepository)
  
  def "allow to cancel client reservation request"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationsRepository.findBy(reservationId) >> Option.of(clientReservationsWithReservation(reservationId))
    
    when:
      def result = cancellingReservationRequest.cancelReservationRequest(new Command(reservationId))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject cancelling client reservation request when there is no such for client"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationsRepository.findBy(reservationId) >> Option.of(noClientReservations())
    
    when:
      def result = cancellingReservationRequest.cancelReservationRequest(new Command(reservationId))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def "fail to cancel client reservation request when cannot find client reservations for such reservation"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationsRepository.findBy(reservationId) >> Option.none()
    
    when:
      def result = cancellingReservationRequest.cancelReservationRequest(new Command(reservationId))
    
    then:
      result.isFailure()
  }
  
}
