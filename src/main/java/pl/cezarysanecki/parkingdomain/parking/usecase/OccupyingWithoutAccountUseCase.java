package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.management.client.ClientFacade;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.VehicleType;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OccupyingWithoutAccountUseCase {

  private final ClientFacade clientFacade;
  private final ParkingFacade parkingFacade;

  public Optional<OccupationId> run(
      PhoneNumber phoneNumber,
      ParkingSpotId parkingSpotId,
      VehicleType vehicleType
  ) {
    if (!parkingFacade.canOccupyNow()) {
      log.debug("cannot occupy parking spot with id {} outside occupying hours", parkingSpotId);
      return Optional.empty();
    }
    return clientFacade.registerClient(ClientType.INDIVIDUAL, phoneNumber)
        .map(clientId -> parkingFacade.occupy(
            new OccupantId(clientId.value()),
            parkingSpotId,
            vehicleType))
        .flatMap(occupationId -> occupationId);
  }

}
