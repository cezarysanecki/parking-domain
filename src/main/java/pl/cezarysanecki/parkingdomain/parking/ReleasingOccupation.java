package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository;

import static pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationEvent.OccupationReleased;

@Slf4j
@RequiredArgsConstructor
class ReleasingOccupation {

  private final OccupationRepository occupationRepository;

  Try<Occupation> release(OccupationId occupationId) {
    log.debug("releasing parking spot using occupation with id {}", occupationId);

    return Try.of(() -> {
      Occupation occupation = occupationRepository.getBy(occupationId);

      OccupationReleased event = occupation.release();
      occupationRepository.publish(event);

      return occupation;
    });
  }

}
