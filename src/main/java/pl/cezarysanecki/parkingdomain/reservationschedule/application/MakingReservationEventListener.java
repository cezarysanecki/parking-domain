package pl.cezarysanecki.parkingdomain.reservationschedule.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationFailed;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationEventListener {

    private final ReservationSchedules reservationSchedules;

    @EventListener
    public void handle(ReservationRequestCreated event) {
        ClientId clientId = event.getClientId();
        ReservationSlot reservationSlot = event.getReservationSlot();

        ReservationSchedule reservationSchedule = event.getParkingSpotId()
                .map(this::load)
                .getOrElse(loadFree(reservationSlot));

        Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(clientId, reservationSlot);
        Match(result).of(
                Case($Left($()), this::publishEvents),
                Case($Right($()), this::publishEvents));
    }

    private Result publishEvents(ReservationMade reservationMade) {
        reservationSchedules.publish(reservationMade);
        log.debug("successfully made reservation with id {}", reservationMade.getReservationId());
        return Success;
    }

    private Result publishEvents(ReservationFailed reservationFailed) {
        reservationSchedules.publish(reservationFailed);
        log.debug("rejected to make reservation with for parking spot with id {}, reason: {}", reservationFailed.getParkingSpotId(), reservationFailed.getReason());
        return Rejection;
    }

    private ReservationSchedule load(ParkingSpotId parkingSpotId) {
        return reservationSchedules.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find parking spot with id: " + parkingSpotId));
    }

    private ReservationSchedule loadFree(ReservationSlot reservationSlot) {
        return reservationSchedules.findFreeFor(reservationSlot)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find free parking spot for slot: " + reservationSlot));
    }

}
