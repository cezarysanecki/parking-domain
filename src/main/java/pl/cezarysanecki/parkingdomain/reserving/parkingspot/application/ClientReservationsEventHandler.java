package pl.cezarysanecki.parkingdomain.reserving.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.WholeParkingSpotReserved;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForWholeParkingSpotSubmitted;
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed;
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.PartOfParkingSpotReserved;

@Slf4j
@RequiredArgsConstructor
public class ClientReservationsEventHandler {

    private final ParkingSpotReservationsRepository parkingSpotReservationsRepository;

    @EventListener
    public void handle(ReservationForPartOfParkingSpotSubmitted reservationSubmitted) {
        ParkingSpotId parkingSpotId = reservationSubmitted.getParkingSpotId();
        ReservationId reservationId = reservationSubmitted.getReservationId();
        VehicleSize vehicleSize = reservationSubmitted.getVehicleSize();

        parkingSpotReservationsRepository.findBy(parkingSpotId)
                .map(parkingSpotReservations -> {
                    Either<ParkingSpotReservationFailed, PartOfParkingSpotReserved> result = parkingSpotReservations.reservePart(reservationId, vehicleSize);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find reservations for parking spot");
                    parkingSpotReservationsRepository.publish(new ParkingSpotReservationFailed(parkingSpotId, reservationId, "cannot find parking spot"));
                });
    }

    @EventListener
    public void handle(ReservationForWholeParkingSpotSubmitted reservationSubmitted) {
        ParkingSpotId parkingSpotId = reservationSubmitted.getParkingSpotId();
        ReservationId reservationId = reservationSubmitted.getReservationId();

        parkingSpotReservationsRepository.findBy(parkingSpotId)
                .map(parkingSpotReservations -> {
                    Either<ParkingSpotReservationFailed, WholeParkingSpotReserved> result = parkingSpotReservations.reserveWhole(reservationId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find reservations for parking spot");
                    parkingSpotReservationsRepository.publish(new ParkingSpotReservationFailed(parkingSpotId, reservationId, "cannot find parking spot"));
                });
    }

    private Result publishEvents(ParkingSpotReservationFailed parkingSpotReservationFailed) {
        log.debug("failed to reserve parking spot with id {}, reason: {}", parkingSpotReservationFailed.getParkingSpotId(), parkingSpotReservationFailed.getReason());
        parkingSpotReservationsRepository.publish(parkingSpotReservationFailed);
        return Result.Rejection.with(parkingSpotReservationFailed.getReason());
    }

    private Result publishEvents(PartOfParkingSpotReserved partOfParkingSpotReserved) {
        log.debug("successfully reserved part of parking spot with id {}", partOfParkingSpotReserved.getParkingSpotId());
        parkingSpotReservationsRepository.publish(partOfParkingSpotReserved);
        return new Result.Success();
    }

    private Result publishEvents(WholeParkingSpotReserved wholeParkingSpotReserved) {
        log.debug("successfully reserved whole parking spot with id {}", wholeParkingSpotReserved.getParkingSpotId());
        parkingSpotReservationsRepository.publish(wholeParkingSpotReserved);
        return new Result.Success();
    }

}