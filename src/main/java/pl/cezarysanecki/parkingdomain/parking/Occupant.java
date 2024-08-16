package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

import java.util.List;

record Occupant(
    OccupantId occupantId,
    List<OccupationId> occupations,
    Version version
) {

  static Occupant newOne(ClientId clientId) {
    return new Occupant(
        new OccupantId(clientId.value()),
        List.of(),
        Version.zero());
  }

  boolean canOccupy(OccupationId occupationId) {
    return occupations.isEmpty();
  }

}
