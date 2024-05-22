package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationRequestsValid {

    private final DateProvider dateProvider;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;
    private final ReservationRequesterRepository reservationRequesterRepository;
    private final int hoursToMakeReservationRequestValid;

    public List<Problem> makeValid() {
        LocalDateTime sinceDate = dateProvider.now().plusHours(hoursToMakeReservationRequestValid);
        Instant sinceDateInstant = sinceDate.toInstant(ZoneOffset.UTC);

        List<ParkingSpotReservationRequests> reservationRequestsList = parkingSpotReservationRequestsRepository.findAllWithRequestsAndValidSince(sinceDateInstant);
        log.debug("found {} reservation requests to make them valid", reservationRequestsList.size());

        var results = reservationRequestsList.map(ParkingSpotReservationRequests::makeValid).toList();

        List<Problem> problems = results
                .filter(Try::isFailure)
                .map(result -> result.getCause().getMessage())
                .map(Problem::new)
                .toList();
        log.debug("found {} problems with requests to make them valid", problems.size());

        List<ReservationRequestConfirmed> events = results
                .filter(Try::isSuccess)
                .flatMap(Try::get);
        log.debug("found {} requests to successfully make them valid", events.size());

        events.forEach(parkingSpotReservationRequestsRepository::publish);

        parkingSpotReservationRequestsRepository.removeAllWithoutRequestsAndValidSince(sinceDateInstant);
        log.debug("removed all lasting reservation requests that are not used");
        reservationRequesterRepository.removeRequestsFromRequesters(events.map(event -> event.validReservationRequest().getReservationRequestId()));
        log.debug("removed all reservation requests being in use by requesters");

        return problems;
    }

    public record Problem(
            String reason
    ) {
    }

}
