package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

public sealed interface ParkingSpotEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    final class ParkingSpotCreated implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        int capacity;

    }

    @Value
    final class VehicleParked implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull Vehicle vehicle;

    }

    @Value
    final class FullyOccupied implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class ReservationFulfilled implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ReservationId reservationId;

    }

    @Value
    final class VehicleParkedEvents implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleParked vehicleParked;
        @NonNull Option<FullyOccupied> fullyOccupied;
        @NonNull Option<ReservationFulfilled> reservationFulfilled;

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>of(vehicleParked)
                    .appendAll(fullyOccupied.toList())
                    .appendAll(reservationFulfilled.toList());
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
    final class CompletelyFreedUp implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class VehicleLeftEvents implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull List<VehicleLeft> vehiclesLeft;
        @NonNull Option<CompletelyFreedUp> completelyFreedUps;

        public static VehicleLeftEvents events(ParkingSpotId parkingSpotId, List<VehicleLeft> vehiclesLeft) {
            return new VehicleLeftEvents(parkingSpotId, vehiclesLeft, Option.none());
        }

        public static VehicleLeftEvents events(ParkingSpotId parkingSpotId, List<VehicleLeft> vehiclesLeft, CompletelyFreedUp completelyFreedUp) {
            return new VehicleLeftEvents(parkingSpotId, vehiclesLeft, Option.of(completelyFreedUp));
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
