package pl.cezarysanecki.parkingdomain._local;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

public class InMemoryEntities {

  @AllArgsConstructor
  public static class OccupationEntity {
    public final OccupationId occupationId;
    public final OccupantId occupantId;
    public final ParkingSpotId parkingSpotId;
    public final SpotUnits occupiedSpace;
  }

  @AllArgsConstructor
  public static class ParkingSpotEntity {
    public final ParkingSpotId parkingSpotId;
    public final ParkingSpotCapacity capacity;
    public int version;
  }

  public record FreeTimeSlotKey(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
  }

  @AllArgsConstructor
  public static class RequestableParkingSpotEntity {
    public final FreeTimeSlotKey freeTimeSlotKey;
    public final int capacity;
    public int version;
  }

  @AllArgsConstructor
  public static class RequesterEntity {
    public final RequesterId requesterId;
    public final int limit;
    public int version;
  }

  @AllArgsConstructor
  public static class RequestEntity {
    public final RequestId requestId;
    public final RequesterId requesterId;
    public final ParkingSpotId parkingSpotId;
    public final TimeSlot timeSlot;
    public final int units;
  }

  @AllArgsConstructor
  public static class OccupantEntity {
    public final OccupantId occupantId;
    public int version;
  }

  @AllArgsConstructor
  public static class ReservationEntity {
    public final ReservationId reservationId;
    public final ReservationOwnerId ownerId;
    public final ParkingSpotId parkingSpotId;
    public final TimeSlot timeSlot;
    public final SpotUnits spotUnits;
  }

}
