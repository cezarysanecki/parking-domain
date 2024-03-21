package pl.cezarysanecki.parkingdomain.reservationeffectiveness;

import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

interface ReservationToMakeEffectiveRepository {

    void save(ReservationToMakeEffectiveEntity entity);

    void delete(ReservationId reservationId);

    Set<ReservationToMakeEffectiveEntity> findValidSince(LocalDateTime dateTime);

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
        public Set<ReservationToMakeEffectiveEntity> findValidSince(LocalDateTime dateTime) {
            return DATABASE.values()
                    .stream()
                    .filter(entity -> entity.validSince.isBefore(dateTime))
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

}
