package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ReservationRequesterId {

  UUID value;

  public static ReservationRequesterId newOne() {
    return new ReservationRequesterId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
