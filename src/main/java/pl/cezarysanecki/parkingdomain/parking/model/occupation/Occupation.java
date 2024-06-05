package pl.cezarysanecki.parkingdomain.parking.model.occupation;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationEvent.OccupationReleased;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Value
public class Occupation {

  @NonNull
  OccupationId occupationId;
  @NonNull
  BeneficiaryId beneficiaryId;
  @NonNull
  ParkingSpotId parkingSpotId;
  @NonNull
  SpotUnits spotUnits;

  public static Occupation newOne(BeneficiaryId beneficiaryId, ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
    return new Occupation(OccupationId.newOne(), beneficiaryId, parkingSpotId, spotUnits);
  }

  public OccupationReleased release() {
    return new OccupationReleased(occupationId, parkingSpotId);
  }

}
