package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationsView;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientsReservationsView;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class InMemoryClientsReservationsViewRepository implements ClientsReservationsView {

    private final Map<ClientId, ClientReservationsViewEntity> database = new ConcurrentHashMap<>();

    @Override
    public ClientReservationsView findFor(ClientId clientId) {
        ClientReservationsViewEntity entity = database.getOrDefault(clientId, new ClientReservationsViewEntity(new HashSet<>()));
        return new ClientReservationsView(clientId.getValue(), entity.getReservations()
                .stream()
                .map(reservationEntity -> new ClientReservationsView.Reservation(
                        reservationEntity.reservationId,
                        reservationEntity.parkingSpotId,
                        reservationEntity.since,
                        reservationEntity.until))
                .collect(Collectors.toUnmodifiableSet()));
    }

    @EventListener
    public void handle(ReservationScheduleEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(), () -> event));
    }

    @EventListener
    public void handle(ParkingSpotEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationFulfilled.class)), this::handle),
                Case($(), () -> event));
    }

    public ReservationScheduleEvent handle(ReservationMade reservationMade) {
        ClientId clientId = reservationMade.getClientId();

        ClientReservationsViewEntity entity = database.getOrDefault(
                clientId, new ClientReservationsViewEntity(new HashSet<>()));
        entity.getReservations().add(new ClientReservationsViewEntity.ReservationEntity(
                reservationMade.getReservationId().getValue(),
                reservationMade.getParkingSpotId().getValue(),
                reservationMade.getReservationSlot().getSince(),
                reservationMade.getReservationSlot().until()));

        database.put(clientId, entity);
        log.debug("creating reservation view for client with id {}", clientId);
        return reservationMade;
    }

    private ReservationScheduleEvent handle(ReservationCancelled reservationCancelled) {
        ClientId clientId = reservationCancelled.getClientId();

        ClientReservationsViewEntity entity = database.getOrDefault(
                clientId, new ClientReservationsViewEntity(new HashSet<>()));
        entity.getReservations().removeIf(
                reservation -> reservation.getReservationId().equals(reservationCancelled.getReservationId().getValue()));

        database.put(clientId, entity);
        log.debug("removing reservation view for client with id {}", clientId);
        return reservationCancelled;
    }

    private ParkingSpotEvent handle(ReservationFulfilled reservationFulfilled) {
        ClientId clientId = reservationFulfilled.getClientId();

        ClientReservationsViewEntity entity = database.getOrDefault(
                clientId, new ClientReservationsViewEntity(new HashSet<>()));
        entity.getReservations().removeIf(
                reservation -> reservation.getParkingSpotId().equals(reservationFulfilled.getParkingSpotId().getValue()));

        database.put(clientId, entity);
        log.debug("removing reservation view for client with id {}", clientId);
        return reservationFulfilled;
    }


}

