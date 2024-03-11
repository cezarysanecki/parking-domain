package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationMade;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents.events;

@Value
public class ParkingSpot {

    ParkingSpotId parkingSpotId;
    int capacity;
    Set<Vehicle> parkedVehicles;
    Reservation reservation;

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new HashSet<>();
        this.reservation = Reservation.none();
    }

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity, Set<Vehicle> parkedVehicles) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new HashSet<>(parkedVehicles);
        this.reservation = Reservation.none();
    }

    public ParkingSpot(
            ParkingSpotId parkingSpotId,
            int capacity,
            Set<Vehicle> parkedVehicles,
            Set<VehicleId> reservations,
            Instant since) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new HashSet<>(parkedVehicles);
        this.reservation = Reservation.of(reservations, Option.of(since));
    }

    public Either<ReleasingFailed, VehicleLeft> release(VehicleId vehicleId) {
        Vehicle foundVehicle = parkedVehicles.stream()
                .filter(parkedVehicle -> parkedVehicle.getVehicleId().equals(vehicleId))
                .findFirst()
                .orElse(null);
        if (foundVehicle == null) {
            return announceFailure(new ReleasingFailed(parkingSpotId));
        }
        return announceSuccess(new VehicleLeft(parkingSpotId, foundVehicle));
    }

    public Either<ParkingFailed, VehicleParkedEvents> park(Vehicle vehicle, Instant now) {
        VehicleId vehicleId = vehicle.getVehicleId();

        if (isNotEnoughSpaceFor(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId));
        }
        if (reservation.isNotFulfillingBy(vehicle, now)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId));
        }

        parkedVehicles.add(vehicle);
        VehicleParked vehicleParked = new VehicleParked(parkingSpotId, vehicle);
        if (reservation.isFor(vehicle)) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new ReservationFulfilled(parkingSpotId, vehicleId)));
        }
        if (isFullOccupied()) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, vehicleParked));
    }

    public Either<ReservationFailed, ReservationMade> reserve(Set<Vehicle> vehicles, Instant since) {
        Set<VehicleId> vehicleIds = vehicles.stream()
                .map(Vehicle::getVehicleId)
                .collect(Collectors.toUnmodifiableSet());
        Integer requestedCapacity = vehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
        if (requestedCapacity > capacity) {
            return announceFailure(new ReservationFailed(parkingSpotId, vehicleIds));
        }
        return announceSuccess(new ReservationMade(parkingSpotId, vehicleIds, since));
    }

    public boolean isEmpty() {
        return currentOccupation() == 0;
    }

    public boolean isParked(VehicleId vehicleId) {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleId)
                .anyMatch(vehicleId::equals);
    }


    private boolean isNotEnoughSpaceFor(Vehicle vehicleSizeUnit) {
        return currentOccupation() + vehicleSizeUnit.getVehicleSizeUnit().getValue() > capacity;
    }

    private boolean isFullOccupied() {
        return capacity == currentOccupation();
    }

    private Integer currentOccupation() {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
    }

    @Value(staticConstructor = "of")
    private static class Reservation {

        Set<VehicleId> bookedFor;
        Option<Instant> since;

        static Reservation none() {
            return new Reservation(Set.of(), Option.none());
        }

        boolean isNotFulfillingBy(Vehicle vehicle, Instant now) {
            return !bookedFor.isEmpty()
                    && !bookedFor.contains(vehicle.getVehicleId())
                    && since.map(instant -> now.plusSeconds(twoHours()).isAfter(instant)).getOrElse(false);
        }

        boolean isFor(Vehicle vehicle) {
            return !bookedFor.isEmpty() && bookedFor.contains(vehicle.getVehicleId());
        }

        private static int twoHours() {
            return 2 * 60 * 60;
        }

    }

}
