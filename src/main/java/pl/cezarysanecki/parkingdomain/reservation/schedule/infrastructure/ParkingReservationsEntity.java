package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import io.vavr.API;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade;

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
                Case($(instanceOf(ReservationForWholeParkingSpotMade.class)), this::handle),
                Case($(instanceOf(ReservationForPartOfParkingSpotMade.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingReservationsEntity handle(ReservationForWholeParkingSpotMade reservationForWholeParkingSpotMade) {
        collection.add(ReservationEntity.individual(
                reservationForWholeParkingSpotMade.getReservationId().getValue(),
                reservationForWholeParkingSpotMade.getReservationPeriod().getDayParts()));
        return this;
    }

    private ParkingReservationsEntity handle(ReservationForPartOfParkingSpotMade reservationForPartOfParkingSpotMade) {
        collection.add(ReservationEntity.collective(
                reservationForPartOfParkingSpotMade.getReservationId().getValue(),
                reservationForPartOfParkingSpotMade.getReservationPeriod().getDayParts(),
                reservationForPartOfParkingSpotMade.getVehicleSizeUnit().getValue()));
        capacity -= reservationForPartOfParkingSpotMade.getVehicleSizeUnit().getValue();
        return this;
    }

}
