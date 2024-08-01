package pl.cezarysanecki.parkingdomain.requesting.api;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class RequesterId {

  UUID value;

  public static RequesterId newOne() {
    return new RequesterId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}

