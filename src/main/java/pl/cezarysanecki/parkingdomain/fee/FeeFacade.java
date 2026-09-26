package pl.cezarysanecki.parkingdomain.fee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.commons.Result;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.fee.api.FeeId;
import pl.cezarysanecki.parkingdomain.fee.api.FeeType;
import pl.cezarysanecki.parkingdomain.fee.api.Money;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

@Slf4j
@RequiredArgsConstructor
public class FeeFacade {

  private final FeeRepository feeRepository;
  private final PriceList priceList;
  private final DateProvider dateProvider;

  @Transactional
  public Result chargeForNotUsedReservation(ClientId clientId, ReservationId reservationId) {
    FeeType type = FeeType.NOT_USED_RESERVATION;
    if (feeRepository.existsFor(type, reservationId)) {
      log.debug("fee {} for reservation {} was already charged", type, reservationId);
      return Result.Rejection;
    }
    Money price = priceList.priceFor(type);
    feeRepository.saveNew(new Fee(FeeId.newOne(), clientId, reservationId, type, price, dateProvider.now()));
    log.debug("charged client {} with {} for not used reservation {}", clientId, price, reservationId);
    return Result.Success;
  }

}
