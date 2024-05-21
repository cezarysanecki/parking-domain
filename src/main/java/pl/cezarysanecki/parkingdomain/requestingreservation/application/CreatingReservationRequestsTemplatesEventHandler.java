package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ParkingSpotReservationRequestsTemplate;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ParkingSpotReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ParkingSpotReservationRequestsTemplateRepository;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequestsTemplatesEventHandler {

    private final ParkingSpotReservationRequestsTemplateRepository repository;

    @EventListener
    public void handle(ParkingSpotAdded event) {
        ParkingSpotReservationRequestsTemplate template = new ParkingSpotReservationRequestsTemplate(
                ParkingSpotReservationRequestsTemplateId.newOne(),
                event.parkingSpotId(),
                event.category(),
                event.capacity());

        log.debug("storing parking spot as reservation requests template with id {}", template.parkingSpotId());
        repository.store(template);
    }

}
