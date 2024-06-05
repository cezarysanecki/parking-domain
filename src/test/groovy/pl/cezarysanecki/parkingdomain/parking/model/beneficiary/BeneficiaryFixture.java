package pl.cezarysanecki.parkingdomain.parking.model.beneficiary;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeneficiaryFixture {

  public static BeneficiaryId anyBeneficiary() {
    return BeneficiaryId.newOne();
  }

}
