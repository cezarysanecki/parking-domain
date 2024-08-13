package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ViewFreeTimeSlotsRepository {

  List<FreeTimeSlotEntry> queryFreeTimeSlots();

  record FreeTimeSlotEntry(
      UUID parkingSpotId,
      ParkingSpotCategory parkingSpotCategory,
      LocalDateTime from,
      LocalDateTime to,
      int spaceLeft
  ) {
  }

}
