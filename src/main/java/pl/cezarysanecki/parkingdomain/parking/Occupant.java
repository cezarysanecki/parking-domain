package pl.cezarysanecki.parkingdomain.parking;

import org.springframework.lang.Nullable;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

record Occupant(
    OccupantId occupantId,
    @Nullable OccupationId occupationId,
    Version version
) {

  static Occupant newOne(ClientId clientId) {
    return new Occupant(
        new OccupantId(clientId.value()),
        null,
        Version.zero());
  }

  boolean canOccupy(OccupationId occupationId) {
    return occupationId == null;
  }

}
