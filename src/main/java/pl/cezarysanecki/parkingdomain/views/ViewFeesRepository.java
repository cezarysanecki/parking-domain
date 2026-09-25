package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ViewFeesRepository {

  List<FeeEntry> queryFees();

  List<FeeEntry> queryFeesFor(ClientId clientId);

  record FeeEntry(
      UUID feeId,
      UUID clientId,
      UUID reservationId,
      String type,
      BigDecimal amount,
      String currency,
      Instant chargedAt
  ) {
  }

}
