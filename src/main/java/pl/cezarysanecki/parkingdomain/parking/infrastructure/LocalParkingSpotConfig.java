package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.integration.ParkingSpotReleased;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationEvent;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationEvent;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationRepository;
import pl.cezarysanecki.parkingdomain.parking.web.BeneficiaryViewRepository;
import pl.cezarysanecki.parkingdomain.parking.web.ParkingSpotViewRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalParkingSpotConfig {

  private final EventPublisher eventPublisher;

  @Bean
  InMemoryBeneficiaryRepository inMemoryBeneficiaryRepository() {
    return new InMemoryBeneficiaryRepository();
  }

  @Bean
  InMemoryOccupationRepository inMemoryOccupationRepository() {
    return new InMemoryOccupationRepository(eventPublisher);
  }

  @Bean
  InMemoryParkingSpotRepository inMemoryParkingSpotRepository() {
    return new InMemoryParkingSpotRepository();
  }

  @Bean
  InMemoryReservationRepository inMemoryReservationRepository() {
    return new InMemoryReservationRepository();
  }

  @RequiredArgsConstructor
  static class InMemoryBeneficiaryRepository implements
      BeneficiaryRepository,
      BeneficiaryViewRepository {

    static final Set<BeneficiaryId> DATABASE = new HashSet<>();

    @Override
    public java.util.List<BeneficiaryView> queryForAllBeneficiaries() {
      return DATABASE.stream()
          .map(beneficiaryId -> {
            java.util.List<UUID> occupations = InMemoryOccupationRepository.DATABASE.stream()
                .filter(entity -> entity.beneficiaryId.equals(beneficiaryId.getValue()))
                .map(entity -> entity.occupationId)
                .toList();
            java.util.List<UUID> reservations = InMemoryReservationRepository.DATABASE.stream()
                .filter(entity -> entity.beneficiaryId.equals(beneficiaryId.getValue()))
                .map(entity -> entity.reservationId)
                .toList();
            return new BeneficiaryView(beneficiaryId.getValue(), occupations, reservations);
          })
          .toList();
    }

    @Override
    public void save(BeneficiaryId beneficiaryId) {
      DATABASE.add(beneficiaryId);
    }

    @Override
    public boolean isPresent(BeneficiaryId beneficiaryId) {
      return DATABASE.contains(beneficiaryId);
    }

  }

  @RequiredArgsConstructor
  static class InMemoryOccupationRepository implements OccupationRepository {

    static final Set<OccupationEntity> DATABASE = new HashSet<>();

    private final EventPublisher eventPublisher;

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
        eventPublisher.publish(new ParkingSpotReleased(released.parkingSpotId()));
      }
    }

    @Override
    public boolean containsOccupationFor(BeneficiaryId beneficiaryId, ParkingSpotId parkingSpotId) {
      return DATABASE.stream()
          .anyMatch(entity -> entity.parkingSpotId.equals(parkingSpotId.getValue())
              && entity.beneficiaryId.equals(beneficiaryId.getValue()));
    }

    static List<OccupationEntity> joinBy(ParkingSpotId parkingSpotId) {
      return DATABASE.stream()
          .filter(entity -> entity.parkingSpotId.equals(parkingSpotId.getValue()))
          .toList();
    }

  }

  @RequiredArgsConstructor
  static class InMemoryParkingSpotRepository implements
      ParkingSpotRepository,
      ParkingSpotViewRepository {

    static final Map<ParkingSpotId, ParkingSpotEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity, ParkingSpotCategory category) {
      DATABASE.put(parkingSpotId, new ParkingSpotEntity(
          parkingSpotId.getValue(),
          capacity.getValue(),
          false,
          category,
          0));
    }

    @Override
    public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
      return Option.of(DATABASE.get(parkingSpotId))
          .map(entity -> {
            List<ReservationEntity> reservations = InMemoryReservationRepository.joinBy(parkingSpotId);
            List<OccupationEntity> occupations = InMemoryOccupationRepository.joinBy(parkingSpotId);
            return entity.toDomain(reservations, occupations);
          });
    }

    @Override
    public Option<ParkingSpot> findBy(ReservationId reservationId) {
      ReservationEntity reservationEntity = InMemoryReservationRepository.joinBy(reservationId);
      return findBy(ParkingSpotId.of(reservationEntity.parkingSpotId));
    }

    @Override
    public void publish(ParkingSpotEvent event) {
      if (event instanceof ParkingSpotEvent.ParkingSpotOccupiedEvents occupiedEvents) {
        publish(occupiedEvents.occupied());
        occupiedEvents.reservationFulfilled()
            .peek(this::publish);
      } else if (event instanceof ParkingSpotEvent.ParkingSpotOccupied occupied) {
        InMemoryOccupationRepository.DATABASE.add(new OccupationEntity(
            occupied.occupation().getOccupationId().getValue(),
            occupied.occupation().getBeneficiaryId().getValue(),
            occupied.occupation().getParkingSpotId().getValue(),
            occupied.occupation().getSpotUnits().getValue()));
      } else if (event instanceof ParkingSpotEvent.ParkingSpotReservationFulfilled reservationFulfilled) {
        InMemoryReservationRepository.DATABASE.removeIf(
            reservationEntity -> reservationEntity.reservationId == reservationFulfilled.reservation().getReservationId().getValue());
      }
    }

    @Override
    public Option<ParkingSpot> findAvailableFor(ParkingSpotCategory category, SpotUnits spotUnits) {
      return Option.ofOptional(DATABASE.values()
              .stream()
              .filter(entity -> entity.category == category && spaceLeft(entity) >= spotUnits.getValue())
              .findAny())
          .flatMap(entity -> findBy(ParkingSpotId.of(entity.parkingSpotId)));
    }

    @Override
    public List<CapacityView> queryForAllAvailableParkingSpots() {
      return DATABASE.values()
          .stream()
          .map(entity -> new CapacityView(
              entity.parkingSpotId,
              entity.category,
              entity.capacity,
              spaceLeft(entity)))
          .filter(capacityView -> capacityView.spaceLeft() > 0)
          .toList();
    }

    static int spaceLeft(ParkingSpotEntity entity) {
      ParkingSpotId parkingSpotId = ParkingSpotId.of(entity.parkingSpotId);

      Integer occupationUsage = InMemoryOccupationRepository.joinBy(parkingSpotId)
          .stream()
          .map(occupationEntity -> occupationEntity.spotUnits)
          .reduce(0, Integer::sum);
      Integer reservationUsage = InMemoryReservationRepository.joinBy(parkingSpotId)
          .stream()
          .map(reservationEntity -> reservationEntity.spotUnits)
          .reduce(0, Integer::sum);

      return entity.capacity - (occupationUsage + reservationUsage);
    }

  }

  @RequiredArgsConstructor
  static class InMemoryReservationRepository implements ReservationRepository {

    static final Set<ReservationEntity> DATABASE = new HashSet<>();

    @Override
    public void saveNew(ReservationId reservationId, ParkingSpotId parkingSpotId, BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
      DATABASE.add(new ReservationEntity(
          reservationId.getValue(),
          parkingSpotId.getValue(),
          beneficiaryId.getValue(),
          spotUnits.getValue()));
    }

    @Override
    public Option<Reservation> findBy(ReservationId reservationId) {
      return Option.ofOptional(DATABASE.stream()
              .filter(entity -> entity.reservationId.equals(reservationId.getValue()))
              .findFirst())
          .map(ReservationEntity::toDomain);
    }

    @Override
    public void publish(ReservationEvent event) {
      if (event instanceof ReservationEvent.ReservationAbandoned abandoned) {
        DATABASE.removeIf(entity -> entity.reservationId.equals(abandoned.reservationId().getValue()));
      }
    }

    static ReservationEntity joinBy(ReservationId reservationId) {
      return DATABASE.stream()
          .filter(entity -> entity.reservationId.equals(reservationId.getValue()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("cannot find reservation with id " + reservationId));
    }

    static List<ReservationEntity> joinBy(ParkingSpotId parkingSpotId) {
      return DATABASE.stream()
          .filter(entity -> entity.parkingSpotId.equals(parkingSpotId.getValue()))
          .toList();
    }

  }

}
