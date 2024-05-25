package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ReservationId {

  UUID value;

  public static ReservationId newOne() {
    return new ReservationId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
