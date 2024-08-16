package pl.cezarysanecki.parkingdomain.views;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotReservationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ReservationEntity;

@RequiredArgsConstructor
class InMemoryViews implements
    ViewCleaningRepository,
    ViewCurrentRequestsRepository,
    ViewCurrentStateOfClientRepository,
    ViewFreeCurrentParkingSpotsRepository,
    ViewFreeTimeSlotsRepository {

  private final int numberOfDrivesAwayToConsiderParkingSpotDirty;

  @Override
  public CleaningView queryCleaning() {
    List<CleaningView.ParkingSpot> parkingSpots = InMemoryRepositories.CLEANING_DATABASE.entrySet()
        .stream()
        .map(entry -> new CleaningView.ParkingSpot(
            entry.getKey().value(),
            entry.getValue()
        ))
        .toList();
    return new CleaningView(
        parkingSpots.stream()
            .filter(entry -> entry.counter() >= numberOfDrivesAwayToConsiderParkingSpotDirty)
            .count(),
        parkingSpots
    );
  }

  @Override
  public List<RequestEntry> queryRequests() {
    return InMemoryRepositories.REQUEST_DATABASE.values()
        .stream()
        .map(entity -> new RequestEntry(
            entity.requestId.value(),
            entity.requesterId.value(),
            entity.parkingSpotId.value(),
            entity.units
        ))
        .toList();
  }

  @Override
  public List<CurrentStateEntry> queryAll() {
    return InMemoryRepositories.CLIENT_DATABASE.values()
        .stream()
        .map(client -> queryFor(client.clientId()))
        .toList();
  }

  @Override
  public CurrentStateEntry queryFor(ClientId clientId) {
    return InMemoryRepositories.CLIENT_DATABASE.values()
        .stream()
        .filter(entity -> entity.clientId().equals(clientId))
        .findFirst()
        .map(client -> new CurrentStateEntry(
            clientId.value(),
            InMemoryRepositories.OCCUPATION_DATABASE.values()
                .stream()
                .filter(entity -> entity.occupantId.value().equals(clientId.value()))
                .map(entity -> entity.occupationId.value())
                .toList(),
            InMemoryRepositories.REQUEST_DATABASE.values()
                .stream()
                .filter(entity -> entity.requesterId.value().equals(clientId.value()))
                .map(entity -> entity.requestId.value())
                .toList(),
            InMemoryRepositories.RESERVATION_DATABASE.values()
                .stream()
                .filter(entity -> entity.ownerId.value().equals(clientId.value())
                    && (entity.status == ReservationEntity.Status.ACTIVE || entity.status == ReservationEntity.Status.STALE))
                .map(entity -> entity.reservationId.value())
                .toList()
        ))
        .orElseThrow(() -> new EntityNotFoundException("cannot find view for client with id " + clientId.value()));
  }

  @Override
  public List<ParkingSpotEntry> queryParkingSpots() {
    return InMemoryRepositories.PARKING_SPOT_DATABASE.values()
        .stream()
        .map(this::createParkingSpotEntry)
        .toList();
  }

  private ParkingSpotEntry createParkingSpotEntry(ParkingSpot parkingSpot) {
    Collection<OccupationEntity> occupations = InMemoryRepositories.OCCUPATION_DATABASE.values();
    Collection<ParkingSpotReservationEntity> reservations = InMemoryRepositories.PARKING_SPOT_RESERVATION_DATABASE.values();

    int occupiedSpace = occupations.stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpot.parkingSpotId()))
        .map(entity -> entity.occupiedSpace)
        .map(SpotUnits::value)
        .reduce(0, Integer::sum);
    int reservedSpace = reservations.stream()
        .filter(entity -> entity.parkingSpotId().equals(parkingSpot.parkingSpotId()))
        .map(ParkingSpotReservationEntity::spotUnits)
        .map(SpotUnits::value)
        .reduce(0, Integer::sum);

    return new ParkingSpotEntry(
        parkingSpot.parkingSpotId().value(),
        parkingSpot.category(),
        parkingSpot.capacity().value() - (occupiedSpace + reservedSpace));
  }

  @Override
  public List<FreeTimeSlotEntry> queryFreeTimeSlots() {
    return InMemoryRepositories.REQUESTABLE_PARKING_SPOT_DATABASE
        .entrySet()
        .stream()
        .map(entry -> new FreeTimeSlotEntry(
            entry.getKey().parkingSpotId().value(),
            InMemoryRepositories.PARKING_SPOT_DATABASE.values()
                .stream()
                .filter(parkingSpot -> parkingSpot.parkingSpotId().equals(entry.getKey().parkingSpotId()))
                .findFirst()
                .map(ParkingSpot::category)
                .orElse(null),
            entry.getKey().timeSlot().from().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            entry.getKey().timeSlot().to().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            entry.getValue().capacity - InMemoryRepositories.REQUEST_DATABASE.values()
                .stream()
                .filter(request -> request.parkingSpotId.equals(entry.getKey().parkingSpotId())
                    && request.timeSlot.equals(entry.getKey().timeSlot()))
                .map(request -> request.units)
                .reduce(0, Integer::sum)
        ))
        .toList();
  }

}
