package pl.cezarysanecki.parkingdomain.requesting.policies;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface CreatingTimeSlotsRepository {

  boolean markAsCreatedFor(LocalDate day);

  boolean wasCreatedFor(LocalDate day);

  @Profile("local")
  @Component
  class InMemoryCreatingTimeSlotsRepository implements CreatingTimeSlotsRepository {

    private static final List<LocalDate> CREATION_DATABASE = new ArrayList<>();

    @Override
    public boolean markAsCreatedFor(LocalDate day) {
      return CREATION_DATABASE.add(day);
    }

    @Override
    public boolean wasCreatedFor(LocalDate day) {
      return CREATION_DATABASE.contains(day);
    }

  }

}
