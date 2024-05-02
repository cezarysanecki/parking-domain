package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface ParkingSpotEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    class Occupied implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        SpotUnits spotUnits;

    }

    @Value
    class FullyOccupied implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;

    }

    @Value
    class OccupiedEvents implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        Occupied occupied;
        @NonNull
        Option<FullyOccupied> fullyOccupied;

        public static OccupiedEvents events(ParkingSpotId parkingSpotId, Occupied occupied) {
            return new OccupiedEvents(parkingSpotId, occupied, Option.none());
        }

        public static OccupiedEvents events(ParkingSpotId parkingSpotId, Occupied occupied, FullyOccupied fullyOccupied) {
            return new OccupiedEvents(parkingSpotId, occupied, Option.of(fullyOccupied));
        }

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>of(occupied).appendAll(fullyOccupied.toList());
        }
    }

    @Value
    class OccupationFailed implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        String reason;

    }

    @Value
    class Released implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        SpotUnits spotUnits;

    }

    @Value
    class CompletelyReleased implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;

    }

    @Value
    class ReleasedEvents implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        ParkingSpotEvent.Released released;
        @NonNull
        Option<CompletelyReleased> completelyFreedUp;

        public static ReleasedEvents events(ParkingSpotId parkingSpotId, Released released) {
            return new ReleasedEvents(parkingSpotId, released, Option.none());
        }

        public static ReleasedEvents events(ParkingSpotId parkingSpotId, Released released, CompletelyReleased completelyReleased) {
            return new ReleasedEvents(parkingSpotId, released, Option.of(completelyReleased));
        }

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>of(released).appendAll(completelyFreedUp.toList());
        }
    }

    @Value
    class ReleasingFailed implements ParkingSpotEvent {

        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        String reason;

    }

}
