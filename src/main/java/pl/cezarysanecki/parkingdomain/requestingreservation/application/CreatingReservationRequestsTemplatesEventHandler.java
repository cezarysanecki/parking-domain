package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplate;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateRepository;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequestsTemplatesEventHandler {

    private final ReservationRequestsTemplateRepository repository;

    @EventListener
    public void handle(ParkingSpotAdded event) {
        ReservationRequestsTemplate template = new ReservationRequestsTemplate(
                ReservationRequestsTemplateId.newOne(),
                event.parkingSpotId(),
                event.category(),
                event.capacity());

        log.debug("storing parking spot as reservation requests template with id {}", template.parkingSpotId());
        repository.save(template);
    }

}
