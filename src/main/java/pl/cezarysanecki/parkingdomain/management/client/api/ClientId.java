package pl.cezarysanecki.parkingdomain.management.client.api;

import java.util.UUID;

public record ClientId(UUID value) {

  public static ClientId newOne() {
    return new ClientId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
