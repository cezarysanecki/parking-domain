package pl.cezarysanecki.parkingdomain.fee.api;

import java.util.UUID;

public record FeeId(UUID value) {

  public static FeeId newOne() {
    return new FeeId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
