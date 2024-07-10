package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@RequiredArgsConstructor
class OccupyingReservedParkingSpot {

  private final ParkingSpotRepository parkingSpotRepository;

  Try<Occupation> occupy(ReservationId reservationId) {
    log.debug("occupying parking spot using reservation with id {}", reservationId);

    return Try.of(() -> {
      ParkingSpot parkingSpot = parkingSpotRepository.getBy(reservationId);

      var result = parkingSpot.occupyUsing(reservationId);

      return Match(result).of(
          Case($Right($()), event -> {
            parkingSpotRepository.publish(event);
            log.debug("successfully occupied parking spot with id {} using reservation with id {}", parkingSpot.getParkingSpotId(), reservationId);
            return event.occupied().occupation();
          }),
          Case($Left($()), exception -> {
            throw exception;
          }));
    }).onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()));
  }

}
