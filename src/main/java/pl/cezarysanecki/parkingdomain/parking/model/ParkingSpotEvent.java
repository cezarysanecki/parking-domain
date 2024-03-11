package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

import java.time.Instant;

public sealed interface ParkingSpotEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    default List<DomainEvent> normalize() {
        return List.of(this);
    }

    @Value
    final class ParkingSpotCreated implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class FullyOccupied implements ParkingSpotEvent {

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
        @NonNull Option<ReservationFulfilled> reservationFulfilled;

        @Override
        public Instant getWhen() {
            return vehicleParked.getWhen();
        }

        public static VehicleParkedEvents events(ParkingSpotId parkingSpotId, VehicleParked vehicleParked) {
            return new VehicleParkedEvents(parkingSpotId, vehicleParked, Option.none(), Option.none());
        }

        public static VehicleParkedEvents events(ParkingSpotId parkingSpotId, VehicleParked vehicleParked, FullyOccupied fullyOccupied) {
            return new VehicleParkedEvents(parkingSpotId, vehicleParked, Option.of(fullyOccupied), Option.none());
        }

        public static VehicleParkedEvents events(ParkingSpotId parkingSpotId, VehicleParked vehicleParked, ReservationFulfilled reservationFulfilled) {
            return new VehicleParkedEvents(parkingSpotId, vehicleParked, Option.none(), Option.of(reservationFulfilled));
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

    }

    @Value
    final class VehicleLeft implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull Vehicle vehicle;

    }

    @Value
    final class ReleasingFailed implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    final class ReservationMade implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;

    }

    @Value
    final class ReservationFailed implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;

    }

    @Value
    final class ReservationFulfilled implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;

    }

}
