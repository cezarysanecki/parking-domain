package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsTemplateRepository.ParkingSpotReservationRequestsTemplate;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequestsTemplatesEventHandler {

    private final DateProvider dateProvider;
    private final ParkingSpotReservationRequestsTemplateRepository repository;

    @EventListener
    public void handle(ParkingSpotAdded event) {
        LocalDate tomorrow = dateProvider.tomorrow();

        List<ParkingSpotReservationRequestsTemplate> templates = Stream.of(
                        TimeSlot.createTimeSlotAtUTC(tomorrow, 7, 17),
                        TimeSlot.createTimeSlotAtUTC(tomorrow, 18, 23))
                .map(timeSlot -> new ParkingSpotReservationRequestsTemplate(
                        event.parkingSpotId(),
                        event.category(),
                        event.capacity()))
                .toList();

        templates.forEach(
                template -> {
                    log.debug("storing parking spot as reservation requests template with id {}", template.parkingSpotId());
                    repository.store(template);
                }
        );
    }

}
