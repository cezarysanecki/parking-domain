package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForWholeParkingSpotStored;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.StoringParkingSpotReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForPartOfParkingSpotStored;

@Slf4j
@RequiredArgsConstructor
public class StoringParkingSpotReservationRequestEventHandler {

    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    @EventListener
    public void handle(RequestForPartOfParkingSpotMade reservationSubmitted) {
        ParkingSpotId parkingSpotId = reservationSubmitted.getParkingSpotId();
        ReservationId reservationId = reservationSubmitted.getReservationId();
        VehicleSize vehicleSize = reservationSubmitted.getVehicleSize();

        parkingSpotReservationRequestsRepository.findBy(parkingSpotId)
                .map(parkingSpotReservations -> {
                    Either<StoringParkingSpotReservationRequestFailed, ReservationRequestForPartOfParkingSpotStored> result = parkingSpotReservations.storeForPart(reservationId, vehicleSize);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find reservations for parking spot");
                    parkingSpotReservationRequestsRepository.publish(new StoringParkingSpotReservationRequestFailed(parkingSpotId, reservationId, "cannot find parking spot"));
                });
    }

    @EventListener
    public void handle(RequestForWholeParkingSpotMade reservationSubmitted) {
        ParkingSpotId parkingSpotId = reservationSubmitted.getParkingSpotId();
        ReservationId reservationId = reservationSubmitted.getReservationId();

        parkingSpotReservationRequestsRepository.findBy(parkingSpotId)
                .map(parkingSpotReservations -> {
                    Either<StoringParkingSpotReservationRequestFailed, ReservationRequestForWholeParkingSpotStored> result = parkingSpotReservations.storeForWhole(reservationId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find reservations for parking spot");
                    parkingSpotReservationRequestsRepository.publish(new StoringParkingSpotReservationRequestFailed(parkingSpotId, reservationId, "cannot find parking spot"));
                });
    }

    private Result publishEvents(StoringParkingSpotReservationRequestFailed parkingSpotReservationFailed) {
        log.debug("failed to reserve parking spot with id {}, reason: {}", parkingSpotReservationFailed.getParkingSpotId(), parkingSpotReservationFailed.getReason());
        parkingSpotReservationRequestsRepository.publish(parkingSpotReservationFailed);
        return Result.Rejection.with(parkingSpotReservationFailed.getReason());
    }

    private Result publishEvents(ReservationRequestForPartOfParkingSpotStored partOfParkingSpotReserved) {
        log.debug("successfully reserved part of parking spot with id {}", partOfParkingSpotReserved.getParkingSpotId());
        parkingSpotReservationRequestsRepository.publish(partOfParkingSpotReserved);
        return new Result.Success<>(partOfParkingSpotReserved.getParkingSpotId());
    }

    private Result publishEvents(ReservationRequestForWholeParkingSpotStored wholeParkingSpotReserved) {
        log.debug("successfully reserved whole parking spot with id {}", wholeParkingSpotReserved.getParkingSpotId());
        parkingSpotReservationRequestsRepository.publish(wholeParkingSpotReserved);
        return new Result.Success<>(wholeParkingSpotReserved.getParkingSpotId());
    }

}
