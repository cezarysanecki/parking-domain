package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ValidReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

@Getter
@AllArgsConstructor
public class ParkingSpot {

    @NonNull
    private final ParkingSpotId parkingSpotId;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private final ParkingSpotCategory category;
    @NonNull
    private Map<OccupationId, Occupation> occupations;
    @NonNull
    private Map<ReservationId, Reservation> reservations;
    private boolean outOfUse;
    @NonNull
    private final Version version;

    public static ParkingSpot newOne(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity, ParkingSpotCategory category) {
        return new ParkingSpot(
                parkingSpotId,
                capacity,
                category,
                HashMap.empty(),
                HashMap.empty(),
                false,
                Version.zero());
    }

    public Try<Occupation> occupyWhole(BeneficiaryId beneficiaryId) {
        return occupy(beneficiaryId, SpotUnits.of(capacity.getValue()));
    }

    public Try<Occupation> occupyUsing(ReservationId reservationId) {
        Option<Reservation> potentialReservation = reservations.get(reservationId);
        if (potentialReservation.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such reservation"));
        }
        Reservation reservation = potentialReservation.get();

        reservations = reservations.remove(reservationId);

        return occupy(reservation.getBeneficiaryId(), reservation.getSpotUnits());
    }

    public Try<Occupation> occupy(BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
        if (outOfUse) {
            return Try.failure(new IllegalArgumentException("out of order"));
        }
        if (exceedsAllowedSpace(spotUnits)) {
            return Try.failure(new IllegalArgumentException("not enough space"));
        }

        Occupation occupation = Occupation.newOne(beneficiaryId, spotUnits);
        occupations = occupations.put(occupation.getOccupationId(), occupation);

        return Try.of(() -> occupation);
    }

    public Try<Occupation> release(OccupationId occupationId) {
        Option<Occupation> potentialOccupation = occupations.get(occupationId);
        if (potentialOccupation.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such occupation"));
        }
        Occupation occupation = potentialOccupation.get();

        occupations = occupations.remove(occupationId);

        return Try.of(() -> occupation);
    }

    public Try<Reservation> reserveUsing(ValidReservationRequest validReservationRequest) {
        ReservationId reservationId = ReservationId.of(validReservationRequest.getReservationRequestId().getValue());
        Reservation reservation = new Reservation(
                BeneficiaryId.of(validReservationRequest.getReservationRequesterId().getValue()),
                reservationId,
                validReservationRequest.getSpotUnits());
        reservations = reservations.put(reservationId, reservation);
        return Try.of(() -> reservation);
    }

    public Try<Result> putIntoService() {
        if (!outOfUse) {
            return Try.failure(new IllegalStateException("is already in service"));
        }
        this.outOfUse = false;
        return Try.success(Result.Success);
    }

    public Try<Result> makeOutOfUse() {
        if (outOfUse) {
            return Try.failure(new IllegalStateException("is already out of use"));
        }
        this.outOfUse = true;
        return Try.success(Result.Success);
    }

    public boolean isFull() {
        return currentOccupation() == capacity.getValue();
    }

    public int spaceLeft() {
        return capacity.getValue() - currentOccupation();
    }

    private int currentOccupation() {
        Integer occupationUnits = occupations.values()
                .map(Occupation::getSpotUnits)
                .map(SpotUnits::getValue)
                .reduceOption(Integer::sum)
                .getOrElse(0);
        Integer reservationUnits = reservations.values()
                .map(Reservation::getSpotUnits)
                .map(SpotUnits::getValue)
                .reduceOption(Integer::sum)
                .getOrElse(0);
        return occupationUnits + reservationUnits;
    }

    private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
        return spotUnits.getValue() + currentOccupation() > capacity.getValue();
    }

}
