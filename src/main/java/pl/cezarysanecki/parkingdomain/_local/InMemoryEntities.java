package pl.cezarysanecki.parkingdomain._local;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.util.List;

public class InMemoryEntities {

  @AllArgsConstructor
  public static class OccupationEntity {
    public final OccupationId occupationId;
    public final OccupantId occupantId;
    public final ParkingSpotId parkingSpotId;
    public final List<ParkingSpotSectionId> sections;
  }

  @AllArgsConstructor
  public static class ParkingSpotSectionEntity {
    public final ParkingSpotId parkingSpotId;
    public final ParkingSpotSectionId sectionId;
    public int version;
  }

  @AllArgsConstructor
  public static class RequestableSectionEntity {
    public final ParkingSpotId parkingSpotId;
    public final ParkingSpotSectionId sectionId;
    public final TimeSlot timeSlot;
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
    public final List<ParkingSpotSectionId> sectionsIds;
  }

}
