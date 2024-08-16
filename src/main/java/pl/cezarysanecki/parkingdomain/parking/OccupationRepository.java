package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReleasedOccupation;

import java.util.Optional;

interface OccupationRepository {

  void saveCheckingVersion(Occupation occupation);

  Optional<ReleasedOccupation> delete(OccupationId occupationId);

}
