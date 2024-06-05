package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;

@Slf4j
@RequiredArgsConstructor
public class ProvidingUsageOfParkingSpot {

  private final ParkingSpotRepository parkingSpotRepository;

  public Try<ParkingSpotId> makeOutOfUse(ParkingSpotId parkingSpotId) {
    ParkingSpot parkingSpot = findBy(parkingSpotId);
    log.debug("found parking spot with id {}", parkingSpot.getParkingSpotId());

    var result = parkingSpot.makeOutOfUse();

    return result
        .onSuccess(event -> {
          log.debug("parking spot with id {} is out of use", parkingSpot.getParkingSpotId());
          parkingSpotRepository.publish(event);
        })
        .map(ParkingSpotEvent.ParkingSpotMadeOutOfUse::parkingSpotId)
        .onFailure(failure -> log.error("failed to make parking spot with id {} out of use", parkingSpot.getParkingSpotId()));
  }

  public Try<ParkingSpotId> putIntoService(ParkingSpotId parkingSpotId) {
    ParkingSpot parkingSpot = findBy(parkingSpotId);
    log.debug("found parking spot with id {}", parkingSpot.getParkingSpotId());

    var result = parkingSpot.putIntoService();
    log.debug("parking spot with id {} is put into service", parkingSpot.getParkingSpotId());

    return result
        .onSuccess(event -> {
          log.debug("parking spot with id {} is in service", parkingSpot.getParkingSpotId());
          parkingSpotRepository.publish(event);
        })
        .map(ParkingSpotEvent.ParkingSpotPutIntoService::parkingSpotId)
        .onFailure(failure -> log.error("failed to put parking spot with id {} into service", parkingSpot.getParkingSpotId()));
  }

  private ParkingSpot findBy(ParkingSpotId parkingSpotId) {
    return parkingSpotRepository.findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
  }

}
