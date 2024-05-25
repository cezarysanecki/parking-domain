package pl.cezarysanecki.parkingdomain.management.client;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ClientId {

  UUID value;

  public static ClientId newOne() {
    return new ClientId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
