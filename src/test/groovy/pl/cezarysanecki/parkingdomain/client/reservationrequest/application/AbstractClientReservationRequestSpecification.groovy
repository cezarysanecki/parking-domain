package pl.cezarysanecki.parkingdomain.client.reservationrequest.application

import pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure.ClientReservationsConfig
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.commons.date.LocalDateProvider
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import spock.lang.Specification

class AbstractClientReservationRequestSpecification extends Specification {
  
  EventPublisher eventPublisher = Mock()
  ClientReservationRequestsRepository repository = Stub()
  
  LocalDateProvider dateProvider = new LocalDateProvider()
  
  ClientReservationsConfig config = new ClientReservationsConfig(eventPublisher, dateProvider, repository)
  
  CancellingReservationRequest cancellingReservationRequest = config.cancellingReservationRequest()
  CreatingReservationRequest creatingReservationRequest = config.creatingReservationRequest()
  
}
