package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

@Value
public class Reservation {

    @NonNull
    BeneficiaryId beneficiaryId;
    @NonNull
    ReservationId reservationId;
    @NonNull
    SpotUnits spotUnits;

    static Reservation newOne(BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
        return new Reservation(beneficiaryId, ReservationId.newOne(), spotUnits);
    }

}
