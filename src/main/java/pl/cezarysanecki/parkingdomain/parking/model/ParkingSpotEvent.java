package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

import java.time.Instant;

public sealed interface ParkingSpotEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    final class ParkingSpotCreated implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        int capacity;

    }

    @Value
    final class FullyOccupied implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class CompletelyFreedUp implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class ReservationFulfilled implements ParkingSpotEvent {

        @NonNull ClientId clientId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class VehicleParked implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull Vehicle vehicle;

    }

    @Value
    final class VehicleParkedEvents implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleParked vehicleParked;
        @NonNull Option<FullyOccupied> fullyOccupied;

        @Override
        public Instant getWhen() {
            return vehicleParked.getWhen();
        }

        public static VehicleParkedEvents events(ParkingSpotId parkingSpotId, VehicleParked vehicleParked) {
            return new VehicleParkedEvents(parkingSpotId, vehicleParked, Option.none());
        }

        public static VehicleParkedEvents events(ParkingSpotId parkingSpotId, VehicleParked vehicleParked, FullyOccupied fullyOccupied) {
            return new VehicleParkedEvents(parkingSpotId, vehicleParked, Option.of(fullyOccupied));
        }

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>of(vehicleParked).appendAll(fullyOccupied.toList());
        }
    }

    @Value
    final class ParkingFailed implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;
        @NonNull String reason;

    }

    @Value
    final class VehicleLeft implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull Vehicle vehicle;

    }

    @Value
    final class VehicleLeftEvents implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull List<VehicleLeft> vehiclesLeft;
        @NonNull Option<CompletelyFreedUp> completelyFreedUps;
        @NonNull Option<ReservationFulfilled> reservationFulfilled;

        public static VehicleLeftEvents events(ParkingSpotId parkingSpotId, List<VehicleLeft> vehiclesLeft) {
            return new VehicleLeftEvents(parkingSpotId, vehiclesLeft, Option.none(), Option.none());
        }

        public static VehicleLeftEvents events(ParkingSpotId parkingSpotId, List<VehicleLeft> vehiclesLeft, CompletelyFreedUp completelyFreedUp) {
            return new VehicleLeftEvents(parkingSpotId, vehiclesLeft, Option.of(completelyFreedUp), Option.none());
        }

        public static VehicleLeftEvents events(ParkingSpotId parkingSpotId, List<VehicleLeft> vehiclesLeft, CompletelyFreedUp completelyFreedUp, ReservationFulfilled reservationFulfilled) {
            return new VehicleLeftEvents(parkingSpotId, vehiclesLeft, Option.of(completelyFreedUp), Option.of(reservationFulfilled));
        }

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>ofAll(vehiclesLeft).appendAll(completelyFreedUps.toList());
        }

    }

    @Value
    final class ReleasingFailed implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull List<VehicleId> vehicleIds;
        @NonNull String reason;

    }

}
