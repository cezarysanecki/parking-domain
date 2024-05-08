package pl.cezarysanecki.parkingdomain.parking.model.beneficiary;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class BeneficiaryId {

    UUID value;

    public static BeneficiaryId newOne() {
        return new BeneficiaryId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
