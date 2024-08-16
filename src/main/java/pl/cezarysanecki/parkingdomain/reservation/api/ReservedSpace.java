package pl.cezarysanecki.parkingdomain.reservation.api;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

public interface ReservedSpace {

  ParkingSpotId parkingSpotId();

  SpotUnits spotUnits();

}
