package pl.cezarysanecki.parkingdomain.reservation.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.PartOfParkingSpotReserved;

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
                    Either<ParkingSpotReservationFailed, PartOfParkingSpotReserved> result = parkingSpotReservations.reserve(reservationId, vehicleSize);
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

}
