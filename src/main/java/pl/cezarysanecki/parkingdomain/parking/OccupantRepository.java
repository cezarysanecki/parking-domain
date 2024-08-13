package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;

interface OccupantRepository {

  Occupant findBy(OccupantId occupantId);

}
