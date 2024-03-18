package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.policy.Rejection;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ReservedParkingSpot implements ParkingSpot {

    @Getter
    private final ParkingSpotBase base;
    private final ReservationId reservation;

    private final List<ReservedParkingSpotPolicy> parkingPolicies;

    public Either<ParkingFailed, VehicleParkedEvents> park(ReservationId reservationId, Vehicle vehicle) {
        ParkingSpotId parkingSpotId = base.getParkingSpotId();

        Option<Rejection> rejection = vehicleCanPark(reservationId, vehicle);
        if (rejection.isEmpty()) {
            Option<FullyOccupied> fullyOccupied = Option.none();
            if (base.isFullyOccupiedWith(vehicle)) {
                fullyOccupied = Option.of(new FullyOccupied(parkingSpotId));
            }
            return announceSuccess(new VehicleParkedEvents(
                    parkingSpotId,
                    new VehicleParked(parkingSpotId, vehicle),
                    fullyOccupied,
                    Option.of(new ReservationFulfilled(parkingSpotId, reservationId))));
        }
        return announceFailure(new ParkingFailed(parkingSpotId, vehicle.getVehicleId(), rejection.get().getReason().getReason()));
    }

    private Option<Rejection> vehicleCanPark(ReservationId reservationId, Vehicle vehicle) {
        return parkingPolicies
                .toStream()
                .map(policy -> policy.apply(this, reservationId, vehicle))
                .find(Either::isLeft)
                .map(Either::getLeft);
    }

    public boolean isReservedFor(ReservationId reservationId) {
        return reservation.equals(reservationId);
    }

    public boolean isNotReservedFor(ReservationId reservationId) {
        return !isReservedFor(reservationId);
    }

}
