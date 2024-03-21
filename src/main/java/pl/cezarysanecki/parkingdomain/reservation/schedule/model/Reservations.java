package pl.cezarysanecki.parkingdomain.reservation.schedule.model;

import io.vavr.control.Option;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;

import java.util.Set;

@Value
public class Reservations {

    Set<Reservation> collection;

    public static Reservations none() {
        return new Reservations(Set.of());
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public boolean contains(ClientId clientId) {
        return collection.stream()
                .map(Reservation::getClientId)
                .anyMatch(clientId::equals);
    }

    public boolean intersects(ReservationSlot slot) {
        return collection.stream()
                .anyMatch(reservation -> reservation.intersects(slot));
    }

    public Option<Reservation> findBy(ReservationId reservationId) {
        return Option.ofOptional(collection.stream()
                .filter(reservation -> reservation.getReservationId().equals(reservationId))
                .findFirst());
    }

}
