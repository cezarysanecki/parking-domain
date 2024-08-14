package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

import java.util.Optional;

interface OccupationRepository {

  void saveCheckingVersion(Occupation occupation);

  Optional<ReleasedOccupation> delete(OccupationId occupationId);

}
