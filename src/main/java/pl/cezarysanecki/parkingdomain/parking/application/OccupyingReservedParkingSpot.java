package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.ParkingSpotOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;

@Slf4j
@RequiredArgsConstructor
public class OccupyingReservedParkingSpot {

  private final ParkingSpotRepository parkingSpotRepository;

  public Try<Occupation> occupy(ReservationId reservationId) {
    ParkingSpot parkingSpot = findBy(reservationId);
    log.debug("occupying parking spot using reservation with id {}", reservationId);

    return parkingSpot.occupyUsing(reservationId)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(parkingSpotRepository::publish)
        .map(ParkingSpotOccupied::occupation);
  }

  private ParkingSpot findBy(ReservationId reservationId) {
    return parkingSpotRepository.findBy(reservationId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot containing reservation with id: " + reservationId));
  }

}
