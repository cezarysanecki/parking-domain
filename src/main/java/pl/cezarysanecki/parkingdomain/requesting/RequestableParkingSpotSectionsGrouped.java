package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.requesting.api.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.List;

public record RequestableParkingSpotSectionsGrouped(
    List<RequestableParkingSpotSection> sections
) {

  static RequestableParkingSpotSectionsGrouped of(ParkingSpot parkingSpot, TimeSlot timeSlot) {
    List<RequestableParkingSpotSection> sections = parkingSpot.sections()
        .stream()
        .map(section -> new RequestableParkingSpotSection(
            parkingSpot.parkingSpotId(), section, timeSlot
        ))
        .toList();
    return new RequestableParkingSpotSectionsGrouped(sections);
  }

  boolean request(ReservationRequestId requesterId) {
    return false;
  }

  boolean release(ReservationRequestId reservationRequestId) {
    return false;
  }

}
