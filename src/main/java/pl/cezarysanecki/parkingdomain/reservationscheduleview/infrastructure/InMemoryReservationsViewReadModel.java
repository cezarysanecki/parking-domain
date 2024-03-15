package pl.cezarysanecki.parkingdomain.reservationscheduleview.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationEvent;
import pl.cezarysanecki.parkingdomain.reservationscheduleview.model.ReservationsView;
import pl.cezarysanecki.parkingdomain.reservationscheduleview.model.ReservationsViews;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationEvent.ReservationCancelled;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationEvent.ReservationMade;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class InMemoryReservationsViewReadModel implements ReservationsViews {

    private final Map<ClientId, ReservationsEntityViewModel> database = new ConcurrentHashMap<>();

    @Override
    public ReservationsView findFor(ClientId clientId) {
        ReservationsEntityViewModel entity = database.getOrDefault(clientId, new ReservationsEntityViewModel(new HashSet<>()));
        return new ReservationsView(clientId.getValue(), entity.getReservations()
                .stream()
                .map(reservationEntity -> new ReservationsView.Reservation(
                        reservationEntity.reservationId,
                        reservationEntity.since,
                        reservationEntity.until))
                .collect(Collectors.toUnmodifiableSet()));
    }

    @EventListener
    public void handle(ReservationEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(), () -> event));
    }

    public ReservationEvent handle(ReservationMade reservationMade) {
        ClientId clientId = reservationMade.getClientId();

        ReservationsEntityViewModel entity = database.getOrDefault(
                clientId, new ReservationsEntityViewModel(new HashSet<>()));
        entity.getReservations().add(new ReservationsEntityViewModel.ReservationEntity(
                reservationMade.getReservationId().getValue(),
                reservationMade.getReservationSlot().getSince(),
                reservationMade.getReservationSlot().until()));

        database.put(clientId, entity);
        log.debug("creating reservation view for client with id {}", clientId);
        return reservationMade;
    }

    private ReservationEvent handle(ReservationCancelled reservationCancelled) {
        ClientId clientId = reservationCancelled.getClientId();

        ReservationsEntityViewModel entity = database.getOrDefault(
                clientId, new ReservationsEntityViewModel(new HashSet<>()));
        entity.getReservations().removeIf(
                reservation -> reservation.getReservationId().equals(reservationCancelled.getReservationId().getValue()));

        database.put(clientId, entity);
        log.debug("removing reservation view for client with id {}", clientId);
        return reservationCancelled;
    }

    @Data
    @AllArgsConstructor
    private static class ReservationsEntityViewModel {

        Set<ReservationEntity> reservations;

        @Data
        @AllArgsConstructor
        private static class ReservationEntity {

            UUID reservationId;
            LocalDateTime since;
            LocalDateTime until;

        }

    }


}

