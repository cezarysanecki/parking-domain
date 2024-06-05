package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository;

import static pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationEvent.OccupationReleased;

@Slf4j
@RequiredArgsConstructor
public class ReleasingOccupation {

  private final OccupationRepository occupationRepository;

  public Try<Occupation> release(OccupationId occupationId) {
    log.debug("releasing parking spot using occupation with id {}", occupationId);

    return Try.of(() -> {
      Occupation occupation = findBy(occupationId);

      OccupationReleased event = occupation.release();
      occupationRepository.publish(event);

      return occupation;
    });
  }

  private Occupation findBy(OccupationId occupationId) {
    return occupationRepository.findBy(occupationId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find occupation with id: " + occupationId));
  }

}
