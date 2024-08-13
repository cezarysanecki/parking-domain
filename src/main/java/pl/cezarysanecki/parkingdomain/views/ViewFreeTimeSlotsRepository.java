package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.util.List;

public interface ViewFreeTimeSlotsRepository {

  List<FreeTimeSlotEntry> queryFreeTimeSlots();

  record FreeTimeSlotEntry(
      ParkingSpotId parkingSpotId,
      ParkingSpotCategory parkingSpotCategory,
      TimeSlot timeSlot,
      int spaceLeft
  ) {
  }

}
