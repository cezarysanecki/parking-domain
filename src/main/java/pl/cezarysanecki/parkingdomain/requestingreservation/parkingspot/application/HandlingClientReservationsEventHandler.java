package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForPartOfParkingSpotRequested;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.WholeRequestParkingSpotReserved;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForWholeParkingSpotRequested;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.RequestingParkingSpotReservationFailed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.PartRequestOfParkingSpotReserved;

@Slf4j
@RequiredArgsConstructor
public class HandlingClientReservationsEventHandler {

    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    @EventListener
    public void handle(ReservationForPartOfParkingSpotRequested reservationSubmitted) {
        ParkingSpotId parkingSpotId = reservationSubmitted.getParkingSpotId();
        ReservationId reservationId = reservationSubmitted.getReservationId();
        VehicleSize vehicleSize = reservationSubmitted.getVehicleSize();

        parkingSpotReservationRequestsRepository.findBy(parkingSpotId)
                .map(parkingSpotReservations -> {
                    Either<RequestingParkingSpotReservationFailed, PartRequestOfParkingSpotReserved> result = parkingSpotReservations.reservePart(reservationId, vehicleSize);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find reservations for parking spot");
                    parkingSpotReservationRequestsRepository.publish(new RequestingParkingSpotReservationFailed(parkingSpotId, reservationId, "cannot find parking spot"));
                });
    }

    @EventListener
    public void handle(ReservationForWholeParkingSpotRequested reservationSubmitted) {
        ParkingSpotId parkingSpotId = reservationSubmitted.getParkingSpotId();
        ReservationId reservationId = reservationSubmitted.getReservationId();

        parkingSpotReservationRequestsRepository.findBy(parkingSpotId)
                .map(parkingSpotReservations -> {
                    Either<RequestingParkingSpotReservationFailed, WholeRequestParkingSpotReserved> result = parkingSpotReservations.reserveWhole(reservationId);
                    return Match(result).of(
                            Case($Left($()), this::publishEvents),
                            Case($Right($()), this::publishEvents));
                })
                .onEmpty(() -> {
                    log.error("cannot find reservations for parking spot");
                    parkingSpotReservationRequestsRepository.publish(new RequestingParkingSpotReservationFailed(parkingSpotId, reservationId, "cannot find parking spot"));
                });
    }

    private Result publishEvents(RequestingParkingSpotReservationFailed parkingSpotReservationFailed) {
        log.debug("failed to reserve parking spot with id {}, reason: {}", parkingSpotReservationFailed.getParkingSpotId(), parkingSpotReservationFailed.getReason());
        parkingSpotReservationRequestsRepository.publish(parkingSpotReservationFailed);
        return Result.Rejection.with(parkingSpotReservationFailed.getReason());
    }

    private Result publishEvents(PartRequestOfParkingSpotReserved partOfParkingSpotReserved) {
        log.debug("successfully reserved part of parking spot with id {}", partOfParkingSpotReserved.getParkingSpotId());
        parkingSpotReservationRequestsRepository.publish(partOfParkingSpotReserved);
        return new Result.Success<>(partOfParkingSpotReserved.getParkingSpotId());
    }

    private Result publishEvents(WholeRequestParkingSpotReserved wholeParkingSpotReserved) {
        log.debug("successfully reserved whole parking spot with id {}", wholeParkingSpotReserved.getParkingSpotId());
        parkingSpotReservationRequestsRepository.publish(wholeParkingSpotReserved);
        return new Result.Success<>(wholeParkingSpotReserved.getParkingSpotId());
    }

}
