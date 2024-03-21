package pl.cezarysanecki.parkingdomain.reservationeffectiveness;

import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MakingReservationEffective {

    private final DateProvider dateProvider;
    private final EventPublisher eventPublisher;
    private final ReservationToMakeEffectiveRepository repository;

    @Scheduled(fixedRate = 5_000L)
    public void makeReservationsEffective() {
        LocalDateTime now = dateProvider.now();

        Set<ReservationToMakeEffectiveEntity> entities = repository.findValidSince(now.plusHours(3));

        List<DomainEvent> events = List.ofAll(
                entities.stream()
                        .map(entity -> new ReservationBecomeEffective(
                                ParkingSpotId.of(entity.parkingSpotId),
                                ReservationId.of(entity.reservationId)))
                        .collect(Collectors.toUnmodifiableSet()));

        eventPublisher.publish(events);
    }

    @EventListener
    public void handle(ReservationMade reservationMade) {
        repository.save(new ReservationToMakeEffectiveEntity(
                reservationMade.getReservationId().getValue(),
                reservationMade.getParkingSpotId().getValue(),
                reservationMade.getReservationSlot().getSince().minusHours(3)));
    }

    @EventListener
    public void handle(ReservationCancelled reservationCancelled) {
        repository.delete(reservationCancelled.getReservationId());
    }

}
