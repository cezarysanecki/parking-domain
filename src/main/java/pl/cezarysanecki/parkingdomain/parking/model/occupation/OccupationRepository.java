package pl.cezarysanecki.parkingdomain.parking.model.occupation;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;

public interface OccupationRepository {

  Option<Occupation> findBy(OccupationId occupationId);

  default Occupation getBy(OccupationId occupationId) {
    return findBy(occupationId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find occupation with id; " + occupationId));
  }

  void publish(OccupationEvent event);

  boolean containsOccupationFor(BeneficiaryId beneficiaryId, ParkingSpotId parkingSpotId);

}
