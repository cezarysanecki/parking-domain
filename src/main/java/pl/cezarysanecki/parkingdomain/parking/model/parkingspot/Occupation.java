package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Value
public class Occupation {

    @NonNull
    BeneficiaryId beneficiaryId;
    @NonNull
    OccupationId occupationId;
    @NonNull
    SpotUnits spotUnits;

    public static Occupation newOne(BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
        return new Occupation(beneficiaryId, OccupationId.newOne(), spotUnits);
    }

}
