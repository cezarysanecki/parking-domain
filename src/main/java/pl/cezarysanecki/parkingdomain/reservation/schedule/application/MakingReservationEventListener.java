package pl.cezarysanecki.parkingdomain.reservation.schedule.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Rejection;
import static pl.cezarysanecki.parkingdomain.commons.commands.Result.Success;
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationEventListener {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ReservingWholeParkingSpotRequestHasOccurred event) {
        ReservationId reservationId = event.getReservationId();
        ReservationPeriod reservationPeriod = event.getReservationPeriod();
        ParkingSpotId parkingSpotId = event.getParkingSpotId();

        Try.of(() -> {
            ParkingSpotReservations parkingSpotReservations = load(parkingSpotId);
            Either<ReservationFailed, ReservationForWholeParkingSpotMade> result = parkingSpotReservations.reserveWhole(reservationId, reservationPeriod);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve whole parking slot", throwable));
    }

    @EventListener
    public void handle(ReservingPartOfParkingSpotRequestHasOccurred event) {
        ReservationId reservationId = event.getReservationId();
        ReservationPeriod reservationPeriod = event.getReservationPeriod();
        VehicleSizeUnit vehicleSizeUnit = event.getVehicleSizeUnit();
        ParkingSpotType parkingSpotType = event.getParkingSpotType();

        Try.of(() -> {
            ParkingSpotReservations parkingSpotReservations = load(parkingSpotType, vehicleSizeUnit);
            Either<ReservationFailed, ReservationForPartOfParkingSpotMade> result = parkingSpotReservations.reservePart(reservationId, reservationPeriod, vehicleSizeUnit);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(throwable -> log.error("Failed to reserve whole parking slot", throwable));
    }

    private Result publishEvents(ReservationForWholeParkingSpotMade reservationForWholeParkingSpotMade) {
        parkingSpotReservationsRepository.publish(reservationForWholeParkingSpotMade);
        log.debug("successfully made reservation for whole parking spot with id {}", reservationForWholeParkingSpotMade.getReservationId());
        return Success;
    }

    private Result publishEvents(ReservationForPartOfParkingSpotMade reservationForPartOfParkingSpotMade) {
        parkingSpotReservationsRepository.publish(reservationForPartOfParkingSpotMade);
        log.debug("successfully made reservation for part of parking spot with id {}", reservationForPartOfParkingSpotMade.getReservationId());
        return Success;
    }

    private Result publishEvents(ReservationFailed reservationFailed) {
        parkingSpotReservationsRepository.publish(reservationFailed);
        log.debug("rejected to make reservation with for parking spot with id {}, reason: {}", reservationFailed.getParkingSpotId(), reservationFailed.getReason());
        return Rejection;
    }

    private ParkingSpotReservations load(ParkingSpotId parkingSpotId) {
        return parkingSpotReservationsRepository.findBy(parkingSpotId)
                .getOrElse(() -> ParkingSpotReservations.none(parkingSpotId));
    }

    private ParkingSpotReservations load(ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
        return parkingSpotReservationsRepository.findFor(parkingSpotType, vehicleSizeUnit)
                .getOrElseThrow(() -> new IllegalArgumentException("cannot find available parking spot " + parkingSpotType + " for vehicle size: " + vehicleSizeUnit));
    }

}
