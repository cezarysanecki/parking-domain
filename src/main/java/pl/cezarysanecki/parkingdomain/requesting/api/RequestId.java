package pl.cezarysanecki.parkingdomain.requesting.api;

import java.util.UUID;

public record RequestId(UUID value) {

  public static RequestId newOne() {
    return new RequestId(UUID.randomUUID());
  }

  public static RequestId none() {
    return null;
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
