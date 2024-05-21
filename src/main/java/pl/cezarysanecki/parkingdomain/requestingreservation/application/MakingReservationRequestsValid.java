package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationRequestsValid {

    private final DateProvider dateProvider;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;
    private final int hoursToMakeReservationRequestValid;

    public List<Problem> makeValid() {
        LocalDateTime sinceDate = dateProvider.now().plusHours(hoursToMakeReservationRequestValid);
        Instant sinceDateInstant = sinceDate.toInstant(ZoneOffset.UTC);

        var results = parkingSpotReservationRequestsRepository.findAllRequestsValidFrom(sinceDateInstant)
                .map(ParkingSpotReservationRequests::makeValid)
                .toList();
        log.debug("Found {} requests to make them valid", results.size());

        List<Problem> problems = results
                .filter(Try::isFailure)
                .map(result -> result.getCause().getMessage())
                .map(Problem::new)
                .toList();
        log.debug("Found {} problems with requests to make them valid", problems.size());

        List<ParkingSpotReservationRequestsEvents> events = results
                .filter(Try::isSuccess)
                .flatMap(Try::get);
        log.debug("Found {} requests to successfully make them valid", events.size());

        events.forEach(parkingSpotReservationRequestsRepository::publish);

        return problems;
    }

    public record Problem(
            String reason
    ) {
    }

}
