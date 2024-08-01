package pl.cezarysanecki.parkingdomain.requesting.api;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ReservationRequestId {

  UUID value;

  public static ReservationRequestId newOne() {
    return new ReservationRequestId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}