package pl.cezarysanecki.parkingdomain.requesting.api;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class RequestId {

  UUID value;

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
