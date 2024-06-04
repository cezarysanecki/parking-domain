package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.UUID;

@AllArgsConstructor
class OccupationEntity {

  UUID occupationId;
  UUID beneficiaryId;
  int spotUnits;

  Occupation toDomain() {
    return new Occupation(
        OccupationId.of(occupationId),
        BeneficiaryId.of(beneficiaryId),
        SpotUnits.of(spotUnits));
  }

}
