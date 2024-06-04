package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;

@Slf4j
@RequiredArgsConstructor
public class ProvidingUsageOfParkingSpot {

  private final ParkingSpotRepository parkingSpotRepository;

  public Try<Result> makeOutOfUse(ParkingSpotId parkingSpotId) {
    ParkingSpot parkingSpot = findBy(parkingSpotId);
    log.debug("found parking spot with id {}", parkingSpot.getParkingSpotId());

    Try<Result> result = parkingSpot.makeOutOfUse();

    return result
        .onSuccess(success -> {
          log.debug("parking spot with id {} is out of use", parkingSpot.getParkingSpotId());
          parkingSpotRepository.save(parkingSpot);
        })
        .onFailure(failure -> log.error("failed to make parking spot with id {} out of use", parkingSpot.getParkingSpotId()));
  }

  public Try<Result> putIntoService(ParkingSpotId parkingSpotId) {
    ParkingSpot parkingSpot = findBy(parkingSpotId);
    log.debug("found parking spot with id {}", parkingSpot.getParkingSpotId());

    Try<Result> result = parkingSpot.putIntoService();
    log.debug("parking spot with id {} is put into service", parkingSpot.getParkingSpotId());

    return result
        .onSuccess(success -> {
          log.debug("parking spot with id {} is in service", parkingSpot.getParkingSpotId());
          parkingSpotRepository.save(parkingSpot);
        })
        .onFailure(failure -> log.error("failed to put parking spot with id {} into service", parkingSpot.getParkingSpotId()));
  }

  private ParkingSpot findBy(ParkingSpotId parkingSpotId) {
    return parkingSpotRepository.findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
  }

}
