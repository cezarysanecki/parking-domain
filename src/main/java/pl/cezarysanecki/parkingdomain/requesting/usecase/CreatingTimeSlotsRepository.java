package pl.cezarysanecki.parkingdomain.requesting.usecase;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.CreatingTimeSlots.CREATING_TIME_SLOTS;

interface CreatingTimeSlotsRepository {

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

  @Profile("!local")
  @Repository
  @RequiredArgsConstructor
  class ProdCreatingTimeSlotsRepository implements CreatingTimeSlotsRepository {

    private final DSLContext create;

    @Override
    public boolean markAsCreatedFor(LocalDate day) {
      int result = create
          .insertInto(CREATING_TIME_SLOTS)
          .set(CREATING_TIME_SLOTS.CREATION_DATE, day)
          .execute();
      return result != 0;
    }

    @Override
    public boolean wasCreatedFor(LocalDate day) {
      return create
          .selectFrom(CREATING_TIME_SLOTS)
          .where(CREATING_TIME_SLOTS.CREATION_DATE.eq(day))
          .execute() > 0;
    }

  }

}
