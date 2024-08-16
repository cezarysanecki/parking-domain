package pl.cezarysanecki.parkingdomain.requesting.api;

import java.util.UUID;

public record RequesterId(UUID value) {

  public static RequesterId newOne() {
    return new RequesterId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}

