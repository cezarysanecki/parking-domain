package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.management.client.ClientFacade;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OccupyingWithoutAccountUseCase {

  private final ClientFacade clientFacade;
  private final ParkingFacade parkingFacade;

  public Optional<OccupationId> run(
      PhoneNumber phoneNumber,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    return clientFacade.registerClient(ClientType.INDIVIDUAL, phoneNumber)
        .toOption()
        .map(clientId -> parkingFacade.occupy(
            new OccupantId(clientId.value()),
            parkingSpotId,
            spotUnits))
        .toJavaOptional()
        .flatMap(occupationId -> occupationId);
  }

}
