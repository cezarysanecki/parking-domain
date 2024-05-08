package pl.cezarysanecki.parkingdomain.parking.model.beneficiary;

import io.vavr.collection.Set;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Reservation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId;

@Getter
@AllArgsConstructor
public class Beneficiary {

    @NonNull
    private final BeneficiaryId beneficiaryId;
    @NonNull
    private Set<ReservationId> reservations;
    @NonNull
    private Set<OccupationId> occupations;

    public Try<Reservation> append(Reservation reservation) {
        reservations = reservations.add(reservation.getReservationId());
        return Try.of(() -> reservation);
    }

    public Try<Occupation> append(Occupation occupation) {
        occupations = occupations.add(occupation.getOccupationId());
        return Try.of(() -> occupation);
    }

    public Try<Reservation> remove(Reservation reservation) {
        if (!reservations.contains(reservation.getReservationId())) {
            return Try.failure(new IllegalStateException("reservation not found"));
        }
        reservations = reservations.remove(reservation.getReservationId());
        return Try.of(() -> reservation);
    }

    public Try<Occupation> remove(Occupation occupation) {
        if (!occupations.contains(occupation.getOccupationId())) {
            return Try.failure(new IllegalStateException("occupation not found"));
        }
        occupations = occupations.remove(occupation.getOccupationId());
        return Try.of(() -> occupation);
    }

}
