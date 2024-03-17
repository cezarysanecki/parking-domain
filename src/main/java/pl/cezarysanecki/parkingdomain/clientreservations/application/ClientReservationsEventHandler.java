package pl.cezarysanecki.parkingdomain.clientreservations.application;

import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationFailed;

@Slf4j
@RequiredArgsConstructor
public class ClientReservationsEventHandler {

    private final ClientReservationsRepository clientReservationsRepository;

    @EventListener
    public void handle(ReservationScheduleEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(instanceOf(ReservationFailed.class)), this::handle),
                Case($(), () -> event));
    }

    private ReservationScheduleEvent handle(ReservationCancelled reservationCancelled) {
        removeReservationRequest(reservationCancelled.getClientId());
        return reservationCancelled;
    }

    private ReservationScheduleEvent handle(ReservationFailed reservationFailed) {
        removeReservationRequest(reservationFailed.getClientId());
        return reservationFailed;
    }

    private void removeReservationRequest(ClientId clientId) {
        clientReservationsRepository.findBy(clientId)
                .map(clientReservations -> {
                    log.debug("cancelling client reservation for client with id {}", clientId);
                    return clientReservationsRepository.publish(new ReservationRequestCancelled(clientId));
                });
    }


}
