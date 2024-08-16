package pl.cezarysanecki.parkingdomain._local;

import pl.cezarysanecki.parkingdomain.management.client.Client;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.FreeTimeSlotKey;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupantEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestableParkingSpotEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequesterEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ReservationEntity;

public class InMemoryRepositories {

  public static final Map<ParkingSpotId, Integer> CLEANING_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ClientId, Client> CLIENT_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ParkingSpotId, ParkingSpot> PARKING_SPOT_DATABASE = new ConcurrentHashMap<>();
  public static final Map<OccupationId, OccupationEntity> OCCUPATION_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ParkingSpotId, ParkingSpotEntity> PARKING_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ParkingSpotId, Integer> TEMPLATES_DATABASE = new ConcurrentHashMap<>();
  public static final Map<FreeTimeSlotKey, RequestableParkingSpotEntity> REQUESTABLE_PARKING_SPOT_DATABASE = new ConcurrentHashMap<>();
  public static final Map<RequesterId, RequesterEntity> REQUESTER_DATABASE = new ConcurrentHashMap<>();
  public static final Map<RequestId, RequestEntity> REQUEST_DATABASE = new ConcurrentHashMap<>();
  public static final Map<OccupantId, OccupantEntity> OCCUPANT_DATABASE = new ConcurrentHashMap<>();
  public static final Map<ReservationId, ReservationEntity> RESERVATION_DATABASE = new ConcurrentHashMap<>();

  public static List<ReservationEntity> getActiveReservationFor(Instant activationDate) {
    return RESERVATION_DATABASE.values()
        .stream()
        .filter(reservationEntity -> reservationEntity.timeSlot.from().isBefore(activationDate))
        .toList();
  }

}
