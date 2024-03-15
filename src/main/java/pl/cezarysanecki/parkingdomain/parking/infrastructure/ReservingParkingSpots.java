package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.collection.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
class ReservingParkingSpots {

    private static final Set<ReservationMade> MADE_RESERVATIONS = new HashSet<>();

    private final DateProvider dateProvider;
    private final EventPublisher eventPublisher;

    @EventListener
    public void handle(ReservationMade reservationMade) {
        operateOn(reservations -> {
            reservations.add(reservationMade);
            return true;
        });
    }

    @EventListener
    public void handle(ReservationCancelled reservationCancelled) {
        operateOn(reservations -> {
            reservations.removeIf(reservation -> reservation.getReservationId().equals(reservationCancelled.getReservationId()));
            return true;
        });
    }

    @Scheduled(fixedRate = 3_000L)
    public void reserveParkingSpots() {
        log.debug("reserving parking spots");

        LocalDateTime now = dateProvider.now();
        List<DomainEvent> events = operateOn(reservations -> {
            Set<ReservationMade> reservationsToPublish = MADE_RESERVATIONS.stream()
                    .filter(reservation -> reservation.getReservationSlot().getSince().minusHours(2).isBefore(now))
                    .collect(Collectors.toUnmodifiableSet());
            log.debug("has {} reservation(s) to make effective", reservationsToPublish.size());

            MADE_RESERVATIONS.removeAll(reservationsToPublish);

            return List.ofAll(reservationsToPublish.stream()
                    .map(reservation -> new ReservationHasBecomeEffective(
                            reservation.getReservationId(),
                            reservation.getParkingSpotId(),
                            reservation.getClientId())));
        });

        eventPublisher.publish(events);
    }

    private synchronized <T> T operateOn(Function<Set<ReservationMade>, T> function) {
        return function.apply(ReservingParkingSpots.MADE_RESERVATIONS);
    }

    @Value
    static class ReservationHasBecomeEffective implements DomainEvent {

        @NonNull ReservationId reservationId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ClientId clientId;

    }

}
