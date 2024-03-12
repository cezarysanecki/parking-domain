package pl.cezarysanecki.parkingdomain.reservation.model;

import io.vavr.control.Option;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

import java.util.Collection;
import java.util.Set;

@Value
public class Reservations {

    Set<Reservation> collection;

    public static Reservations none() {
        return new Reservations(Set.of());
    }

    public boolean contains(VehicleId vehicleId) {
        return collection.stream()
                .map(Reservation::getVehicles)
                .flatMap(Collection::stream)
                .map(Vehicle::getVehicleId)
                .anyMatch(vehicleId::equals);
    }

    public boolean intersects(ReservationSlot slot) {
        return collection.stream()
                .anyMatch(reservation -> reservation.intersects(slot));
    }

    Option<Reservation> findBy(ReservationId reservationId) {
        return Option.ofOptional(collection.stream()
                .filter(reservation -> reservation.getReservationId().equals(reservationId))
                .findFirst());
    }

}
