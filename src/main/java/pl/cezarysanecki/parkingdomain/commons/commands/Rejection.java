package pl.cezarysanecki.parkingdomain.commons.commands;

import lombok.NonNull;
import lombok.Value;

@Value
public class Rejection {

  @Value
  public static class Reason {
    @NonNull
    String reason;
  }

  @NonNull
  Reason reason;

  public static Rejection withReason(String reason) {
    return new Rejection(new Reason(reason));
  }

}
