package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.application.CancellingReservationEventListener;
import pl.cezarysanecki.parkingdomain.reservation.application.MakingReservationEventListener;
import pl.cezarysanecki.parkingdomain.reservation.application.ParkingSpotReservationEventListener;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ParkingSpotReservationsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    public ParkingSpotReservationEventListener parkingSpotReservationEventListener(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new ParkingSpotReservationEventListener(parkingSpotReservationsRepository);
    }

    @Bean
    public MakingReservationEventListener makingParkingSlotReservation(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new MakingReservationEventListener(parkingSpotReservationsRepository);
    }

    @Bean
    public CancellingReservationEventListener cancellingReservationEventListener(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new CancellingReservationEventListener(parkingSpotReservationsRepository);
    }

    @Bean
    @Profile("local")
    public ParkingSpotReservationsRepository inMemoryReservationSchedules() {
        return new InMemoryReservationScheduleRepository(eventPublisher);
    }

}
