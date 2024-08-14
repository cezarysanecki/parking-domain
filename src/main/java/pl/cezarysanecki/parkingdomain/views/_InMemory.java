package pl.cezarysanecki.parkingdomain.views;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpot;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

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
                .toList()
        ))
        .orElseThrow(() -> new EntityNotFoundException("cannot find view for client with id " + clientId.value()));
  }

  @Override
  public List<ParkingSpotEntry> queryParkingSpots() {
    return InMemoryRepositories.PARKING_SPOT_DATABASE.values()
        .stream()
        .map(entity -> new ParkingSpotEntry(
            entity.parkingSpotId().value(),
            entity.category(),
            entity.sections().size() - InMemoryRepositories.OCCUPATION_DATABASE.values()
                .stream()
                .filter(occupationEntity -> occupationEntity.parkingSpotId.equals(entity.parkingSpotId()))
                .map(occupationEntity -> occupationEntity.sections)
                .mapToInt(Collection::size)
                .sum())
        )
        .toList();
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
            entry.getKey().timeSlot().from().atZone(ZoneId.of("UTC")).toLocalDateTime(),
            entry.getKey().timeSlot().to().atZone(ZoneId.of("UTC")).toLocalDateTime(),
            entry.getValue().capacity - (int) InMemoryRepositories.REQUEST_DATABASE.values()
                .stream()
                .filter(request -> request.parkingSpotId.equals(entry.getKey().parkingSpotId())
                    && request.timeSlot.equals(entry.getKey().timeSlot()))
                .count()
        ))
        .toList();
  }

}
