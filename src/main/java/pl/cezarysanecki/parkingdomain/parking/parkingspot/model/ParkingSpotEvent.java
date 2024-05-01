package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.SpotUnits;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

public interface ParkingSpotEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    class ParkingSpotOccupied implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;
        @NonNull
        SpotUnits spotUnits;

    }

    @Value
    class FullyOccupied implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    class ParkingSpotOccupiedEvents implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ParkingSpotOccupied parkingSpotOccupied;
        @NonNull Option<FullyOccupied> fullyOccupied;

        public static ParkingSpotOccupiedEvents events(ParkingSpotId parkingSpotId, ParkingSpotOccupied parkingSpotOccupied) {
            return new ParkingSpotOccupiedEvents(parkingSpotId, parkingSpotOccupied, Option.none());
        }

        public static ParkingSpotOccupiedEvents events(ParkingSpotId parkingSpotId, ParkingSpotOccupied parkingSpotOccupied, FullyOccupied fullyOccupied) {
            return new ParkingSpotOccupiedEvents(parkingSpotId, parkingSpotOccupied, Option.of(fullyOccupied));
        }

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>of(parkingSpotOccupied).appendAll(fullyOccupied.toList());
        }
    }

    @Value
    class ParkingSpotOccupationFailed implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;
        @NonNull String reason;

    }

    @Value
    class ParkingSpotLeft implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;

    }

    @Value
    class CompletelyFreedUp implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    class ParkingSpotLeftEvents implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull ParkingSpotLeft parkingSpotLeft;
        @NonNull Option<CompletelyFreedUp> completelyFreedUp;

        public static ParkingSpotLeftEvents events(ParkingSpotId parkingSpotId, ParkingSpotLeft parkingSpotLeft) {
            return new ParkingSpotLeftEvents(parkingSpotId, parkingSpotLeft, Option.none());
        }

        public static ParkingSpotLeftEvents events(ParkingSpotId parkingSpotId, ParkingSpotLeft parkingSpotLeft, CompletelyFreedUp completelyFreedUp) {
            return new ParkingSpotLeftEvents(parkingSpotId, parkingSpotLeft, Option.of(completelyFreedUp));
        }

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>of(parkingSpotLeft).appendAll(completelyFreedUp.toList());
        }
    }

    @Value
    class ParkingSpotLeavingOutFailed implements ParkingSpotEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleId vehicleId;
        @NonNull String reason;

    }

}
