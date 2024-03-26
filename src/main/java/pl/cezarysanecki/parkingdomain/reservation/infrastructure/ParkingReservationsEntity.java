package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import io.vavr.API;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
class ParkingReservationsEntity {

    final UUID parkingSpotId;
    final ParkingSpotType parkingSpotType;
    int capacity;
    final Set<ReservationEntity> collection;

    ParkingReservationsEntity(UUID parkingSpotId, ParkingSpotType parkingSpotType, int capacity) {
        this.parkingSpotId = parkingSpotId;
        this.parkingSpotType = parkingSpotType;
        this.capacity = capacity;
        this.collection = new HashSet<>();
    }

    ParkingReservationsEntity handle(ParkingSpotReservationsEvent event) {
        return API.Match(event).of(
                API.Case(API.$(instanceOf(ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotReservationsEvent.ReservationCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingReservationsEntity handle(ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade reservationForWholeParkingSpotMade) {
        collection.add(ReservationEntity.individual(
                reservationForWholeParkingSpotMade.getReservationId().getValue(),
                reservationForWholeParkingSpotMade.getReservationPeriod().getDayParts()));
        return this;
    }

    private ParkingReservationsEntity handle(ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade reservationForPartOfParkingSpotMade) {
        collection.add(ReservationEntity.collective(
                reservationForPartOfParkingSpotMade.getReservationId().getValue(),
                reservationForPartOfParkingSpotMade.getReservationPeriod().getDayParts(),
                reservationForPartOfParkingSpotMade.getVehicleSizeUnit().getValue()));
        capacity -= reservationForPartOfParkingSpotMade.getVehicleSizeUnit().getValue();
        return this;
    }

    private ParkingReservationsEntity handle(ParkingSpotReservationsEvent.ReservationCancelled reservationCancelled) {
        collection.removeIf(reservationEntity -> reservationEntity.reservationId.equals(reservationCancelled.getReservationId().getValue()));
        return this;
    }

}
