package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeftEvents;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpot {

    @Getter
    private final ParkingSpotId parkingSpotId;
    private final int capacity;
    private final Set<Vehicle> parkedVehicles;
    private final Option<ParkingSpotReservation> reservation;
    private final boolean outOfOrder;

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity) {
        this(parkingSpotId, capacity, Set.of(), Option.none(), false);
    }

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity, ParkingSpotReservation reservation) {
        this(parkingSpotId, capacity, Set.of(), Option.of(reservation), false);
    }

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity, Set<Vehicle> parkedVehicles, boolean outOfOrder) {
        this(parkingSpotId, capacity, parkedVehicles, Option.none(), outOfOrder);
    }

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity, Set<Vehicle> parkedVehicles, boolean outOfOrder, ParkingSpotReservation reservation) {
        this(parkingSpotId, capacity, parkedVehicles, Option.of(reservation), outOfOrder);
    }

    public Either<ParkingFailed, VehicleParkedEvents> park(ClientId clientId, Vehicle vehicle) {
        VehicleId vehicleId = vehicle.getVehicleId();

        if (outOfOrder) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "parking on out of order parking spot is forbidden"));
        }
        if (isParked(vehicleId)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "vehicle is already parked on parking spot"));
        }
        if (thereIsNotEnoughSpaceFor(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "not enough space on parking spot"));
        }
        if (isNotReservedFor(clientId)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "parking spot is not reserved for this client"));
        }

        VehicleParked vehicleParked = new VehicleParked(parkingSpotId, vehicle);
        if (isFullyOccupied(vehicle)) {
            return announceSuccess(VehicleParkedEvents.events(parkingSpotId, vehicleParked, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(VehicleParkedEvents.events(parkingSpotId, vehicleParked));
    }

    public Either<ReleasingFailed, VehicleLeftEvents> releaseBy(VehicleId vehicleId) {
        Vehicle foundVehicle = parkedVehicles.stream()
                .filter(parkedVehicle -> parkedVehicle.getVehicleId().equals(vehicleId))
                .findFirst()
                .orElse(null);
        if (foundVehicle == null) {
            return announceFailure(new ReleasingFailed(parkingSpotId, List.of(vehicleId), "vehicle not park on this spot"));
        }

        VehicleLeft vehicleLeft = new VehicleLeft(parkingSpotId, foundVehicle);
        if (isCompletelyFreedUp(foundVehicle)) {
            if (reservation.isDefined()) {
                ParkingSpotReservation parkingSpotReservation = reservation.get();
                return announceSuccess(VehicleLeftEvents.events(parkingSpotId,
                        List.of(vehicleLeft), new CompletelyFreedUp(parkingSpotId),
                        new ReservationFulfilled(parkingSpotId, parkingSpotReservation.getClientId(), parkingSpotReservation.getReservationId())));
            }
            return announceSuccess(VehicleLeftEvents.events(parkingSpotId, List.of(vehicleLeft), new CompletelyFreedUp(parkingSpotId)));
        }
        return announceSuccess(VehicleLeftEvents.events(parkingSpotId, List.of(vehicleLeft)));
    }

    public Either<ReleasingFailed, VehicleLeftEvents> releaseAll() {
        if (parkedVehicles.isEmpty()) {
            return announceFailure(new ReleasingFailed(parkingSpotId, List.empty(), "parking spot is empty"));
        }

        List<VehicleLeft> vehiclesLeft = List.ofAll(parkedVehicles.stream()
                .map(parkedVehicle -> new VehicleLeft(parkingSpotId, parkedVehicle))
                .toList());
        if (reservation.isDefined()) {
            ParkingSpotReservation parkingSpotReservation = reservation.get();
            return announceSuccess(VehicleLeftEvents.events(parkingSpotId,
                    vehiclesLeft, new CompletelyFreedUp(parkingSpotId),
                    new ReservationFulfilled(parkingSpotId, parkingSpotReservation.getClientId(), parkingSpotReservation.getReservationId())));
        }
        return announceSuccess(VehicleLeftEvents.events(parkingSpotId, vehiclesLeft, new CompletelyFreedUp(parkingSpotId)));
    }

    public boolean isEmpty() {
        return parkedVehicles.isEmpty();
    }

    public boolean isFull() {
        return currentOccupation() == capacity;
    }

    public boolean isParked(VehicleId vehicleId) {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleId)
                .anyMatch(parkedVehicleId -> parkedVehicleId.equals(vehicleId));
    }

    private boolean isNotReservedFor(ClientId clientId) {
        return !reservation.isEmpty() && !reservation.get().getClientId().equals(clientId);
    }

    private boolean thereIsNotEnoughSpaceFor(Vehicle vehicle) {
        return currentOccupation() + vehicle.getVehicleSizeUnit().getValue() > capacity;
    }

    private boolean isFullyOccupied(Vehicle vehicle) {
        return currentOccupation() + vehicle.getVehicleSizeUnit().getValue() == capacity;
    }

    private boolean isCompletelyFreedUp(Vehicle foundVehicle) {
        return currentOccupation() - foundVehicle.getVehicleSizeUnit().getValue() == 0;
    }

    private Integer currentOccupation() {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
    }

}
