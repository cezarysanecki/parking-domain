package pl.cezarysanecki.parkingdomain.parking;

import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;

import java.time.Instant;

interface OccupantRepository {

  Occupant findBy(OccupantId occupantId, Instant activationDateOfReservations);

  void saveNew(Occupant occupant);

}
