package pl.cezarysanecki.parkingdomain._local;

import pl.cezarysanecki.parkingdomain.management.client.Client;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotSectionEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestableSectionEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequesterEntity;

public class InMemoryRepositories {

  public static final Map<ParkingSpotId, Integer> CLEANING_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ClientId, Client> CLIENT_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ParkingSpotId, ParkingSpot> PARKING_SPOT_DATABASE = new ConcurrentHashMap<>();
  public static final Map<OccupationId, OccupationEntity> OCCUPATION_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ParkingSpotSectionId, ParkingSpotSectionEntity> PARKING_SPOT_SECTION_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ParkingSpotSectionId, RequestableSectionEntity> REQUESTABLE_SECTION_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ParkingSpotId, List<ParkingSpotSectionId>> TEMPLATES_DATABASE = new ConcurrentHashMap<>();
  public static final Map<RequesterId, RequesterEntity> REQUESTER_DATABASE = new ConcurrentHashMap<>();
  public static final Map<RequestId, RequestEntity> REQUEST_DATABASE = new ConcurrentHashMap<>();

}
