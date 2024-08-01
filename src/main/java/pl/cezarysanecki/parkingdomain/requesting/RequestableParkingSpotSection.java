package pl.cezarysanecki.parkingdomain.requesting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

@Getter
@AllArgsConstructor
public class RequestableParkingSpotSection {

  private ParkingSpotId parkingSpotId;
  private ParkingSpotSectionId parkingSpotSectionId;
  private TimeSlot timeSlot;

}
