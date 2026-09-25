package pl.cezarysanecki.parkingdomain.fee;

import pl.cezarysanecki.parkingdomain.fee.api.FeeId;
import pl.cezarysanecki.parkingdomain.fee.api.FeeType;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.util.Map;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.FeeEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.FEE_DATABASE;

class InMemoryFeeRepository implements FeeRepository {

  private static final Map<FeeId, FeeEntity> DATABASE = FEE_DATABASE;

  @Override
  public void saveNew(Fee fee) {
    DATABASE.put(fee.feeId(), new FeeEntity(
        fee.feeId(),
        fee.clientId(),
        fee.reservationId(),
        fee.type(),
        fee.amount(),
        fee.chargedAt()));
  }

  @Override
  public boolean existsFor(FeeType type, ReservationId reservationId) {
    return DATABASE.values()
        .stream()
        .anyMatch(entity -> entity.type() == type && entity.reservationId().equals(reservationId));
  }

}
