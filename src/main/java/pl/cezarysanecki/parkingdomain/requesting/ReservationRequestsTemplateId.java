package pl.cezarysanecki.parkingdomain.requesting;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ReservationRequestsTemplateId {

  UUID value;

  public static ReservationRequestsTemplateId newOne() {
    return new ReservationRequestsTemplateId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
