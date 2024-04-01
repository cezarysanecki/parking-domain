package pl.cezarysanecki.parkingdomain.reservation.client.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

import java.util.Set;
import java.util.UUID;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientEvent.ReservationRequestSubmissionFailed;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientEvent.ReservationRequestSubmitted;

@Value
public class Client {

    ClientId clientId;
    Set<ReservationId> reservations;

    public Either<ReservationRequestSubmissionFailed, ReservationRequestSubmitted> createRequest(ParkingSpotId parkingSpotId, VehicleSize vehicleSize) {
        if (willBeTooManyRequests()) {
            return announceFailure(new ReservationRequestSubmissionFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new ReservationRequestSubmitted(new ReservationRequest(clientId, ReservationId.of(UUID.randomUUID()), parkingSpotId, vehicleSize)));
    }

    private boolean willBeTooManyRequests() {
        return reservations.size() + 1 > 1;
    }

}
