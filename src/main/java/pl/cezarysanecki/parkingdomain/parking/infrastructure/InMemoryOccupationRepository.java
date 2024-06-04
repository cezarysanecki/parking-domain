package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationEvent;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository;

import java.util.HashSet;
import java.util.Set;

class InMemoryOccupationRepository implements OccupationRepository {

  static final Set<OccupationEntity> DATABASE = new HashSet<>();

  @Override
  public Option<Occupation> findBy(OccupationId occupationId) {
    return Option.ofOptional(DATABASE.stream()
            .filter(entity -> entity.occupationId.equals(occupationId.getValue()))
            .findFirst())
        .map(OccupationEntity::toDomain);
  }

  @Override
  public void publish(OccupationEvent event) {
    if (event instanceof OccupationEvent.OccupationReleased released) {
      DATABASE.removeIf(entity -> entity.occupationId.equals(released.occupationId().getValue()));
    }
  }

}
