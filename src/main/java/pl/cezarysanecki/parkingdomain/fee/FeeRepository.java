package pl.cezarysanecki.parkingdomain.fee;

import pl.cezarysanecki.parkingdomain.fee.api.FeeType;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

interface FeeRepository {

  void saveNew(Fee fee);

  boolean existsFor(FeeType type, ReservationId reservationId);

}
