package pl.cezarysanecki.parkingdomain.fee;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.fee.api.FeeType;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Fee.FEE;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdFeeRepository implements FeeRepository {

  private final DSLContext create;

  @Override
  public void saveNew(Fee fee) {
    create.insertInto(FEE)
        .set(FEE.ID, fee.feeId().value())
        .set(FEE.CLIENT, fee.clientId().value())
        .set(FEE.RESERVATION, fee.reservationId().value())
        .set(FEE.TYPE, fee.type().name())
        .set(FEE.AMOUNT, fee.amount().amount())
        .set(FEE.CURRENCY, fee.amount().currency().getCurrencyCode())
        .set(FEE.CHARGED_AT, LocalDateTime.ofInstant(fee.chargedAt(), ZoneId.systemDefault()))
        .execute();
  }

  @Override
  public boolean existsFor(FeeType type, ReservationId reservationId) {
    return create.fetchExists(
        create.selectFrom(FEE)
            .where(FEE.TYPE.eq(type.name()))
            .and(FEE.RESERVATION.eq(reservationId.value())));
  }

}
