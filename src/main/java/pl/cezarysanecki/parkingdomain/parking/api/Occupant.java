package pl.cezarysanecki.parkingdomain.parking.api;

import java.util.UUID;

public record Occupant(UUID value) {

  public static Occupant none() {
    return new Occupant(null);
  }

  public UUID id() {
    return value;
  }

}
