package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.SpotUnits;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;

public interface VehicleEvent extends DomainEvent {

    VehicleId vehicleId();

    @Value
    class VehicleParked implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull
        SpotUnits spotUnits;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    class FulfilledReservation implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull ReservationId reservationId;

    }

    @Value
    class VehicleParkedEvents implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull VehicleParked vehicleParked;
        @NonNull Option<FulfilledReservation> fulfilledReservation;

        public static VehicleParkedEvents events(VehicleId vehicleId, VehicleParked vehicleParked) {
            return new VehicleParkedEvents(vehicleId, vehicleParked, Option.none());
        }

        public static VehicleParkedEvents events(VehicleId vehicleId, VehicleParked vehicleParked, FulfilledReservation fulfilledReservation) {
            return new VehicleParkedEvents(vehicleId, vehicleParked, Option.of(fulfilledReservation));
        }

        @Override
        public List<DomainEvent> normalize() {
            return List.<DomainEvent>of(vehicleParked).appendAll(fulfilledReservation.toList());
        }

    }

    @Value
    class VehicleParkingFailed implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull String reason;

    }

    @Value
    class VehicleDroveAway implements VehicleEvent {

        @NonNull VehicleId vehicleId;

    }

    @Value
    class VehicleDrivingAwayFailed implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull String reason;

    }

}
