package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ValidReservationRequest;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationRequestsValid {

    private final EventPublisher eventPublisher;
    private final DateProvider dateProvider;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;
    private final int hoursToMakeReservationRequestValid;

    public List<Problem> makeValid() {
        Instant sinceDate = Instant.from(dateProvider.now().plusHours(hoursToMakeReservationRequestValid));

        List<ReservationRequestConfirmed> events = parkingSpotReservationRequestsRepository.findAllRequestsValidFrom(sinceDate)
                .flatMap(parkingSpotReservationRequests -> {
                    List<ValidReservationRequest> validReservationRequests = parkingSpotReservationRequests.makeValid();
                    return validReservationRequests
                            .map(validReservationRequest -> new ReservationRequestConfirmed(
                                    parkingSpotReservationRequests.getParkingSpotId(), validReservationRequest));
                })
                .toList();

        log.debug("Found {} requests to make them valid", events.size());

        return events
                .flatMap(event -> {
                    try {
                        eventPublisher.publish(event);
                        return Option.none();
                    } catch (Exception e) {
                        return Option.of(new Problem(e.getMessage()));
                    }
                })
                .toList();

    }

    public record Problem(
            String reason
    ) {
    }

}
