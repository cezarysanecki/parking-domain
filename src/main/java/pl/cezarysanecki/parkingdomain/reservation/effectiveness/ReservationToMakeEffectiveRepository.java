package pl.cezarysanecki.parkingdomain.reservation.effectiveness;

import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

interface ReservationToMakeEffectiveRepository {

    void save(ReservationToMakeEffectiveEntity entity);

    void delete(ReservationId reservationId);

    boolean contains(ReservationId reservationId);

    Set<ReservationToMakeEffectiveEntity> findValidSince(LocalDateTime dateTime);

    void deleteAll(Set<ReservationId> reservations);

    class InMemoryReservationToMakeEffectiveRepository implements ReservationToMakeEffectiveRepository {

        private static final Map<ReservationId, ReservationToMakeEffectiveEntity> DATABASE = new ConcurrentHashMap<>();

        @Override
        public void save(ReservationToMakeEffectiveEntity entity) {
            DATABASE.put(ReservationId.of(entity.reservationId), entity);
        }

        @Override
        public void delete(ReservationId reservationId) {
            DATABASE.remove(reservationId);
        }

        @Override
        public boolean contains(ReservationId reservationId) {
            return DATABASE.get(reservationId) != null;
        }

        @Override
        public Set<ReservationToMakeEffectiveEntity> findValidSince(LocalDateTime dateTime) {
            return DATABASE.values()
                    .stream()
                    .filter(entity -> entity.validSince.isBefore(dateTime))
                    .collect(Collectors.toUnmodifiableSet());
        }

        @Override
        public void deleteAll(Set<ReservationId> reservations) {
            reservations.forEach(DATABASE::remove);
        }

    }

}
