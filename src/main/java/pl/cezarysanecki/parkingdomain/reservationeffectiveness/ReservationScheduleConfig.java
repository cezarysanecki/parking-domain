package pl.cezarysanecki.parkingdomain.reservationeffectiveness;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.CancellingReservation;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.MakingReservationEventListener;
import pl.cezarysanecki.parkingdomain.reservationschedule.application.ParkingSpotReservationEventListener;
import pl.cezarysanecki.parkingdomain.reservationschedule.infrastructure.DomainModelMapper;
import pl.cezarysanecki.parkingdomain.reservationschedule.infrastructure.InMemoryReservationScheduleRepository;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules;

@Slf4j
@Configuration
@EnableScheduling
public class ReservationScheduleConfig {

    private final EventPublisher eventPublisher;
    private final DomainModelMapper domainModelMapper;

    public ReservationScheduleConfig(EventPublisher eventPublisher, DateProvider dateProvider) {
        this.eventPublisher = eventPublisher;
        this.domainModelMapper = new DomainModelMapper(dateProvider);
    }

    @Bean
    public ParkingSpotReservationEventListener parkingSpotReservationEventListener(ReservationSchedules reservationSchedules) {
        return new ParkingSpotReservationEventListener(reservationSchedules);
    }

    @Bean
    public MakingReservationEventListener makingParkingSlotReservation(ReservationSchedules reservationSchedules) {
        return new MakingReservationEventListener(reservationSchedules);
    }

    @Bean
    public CancellingReservation cancellingReservation(ReservationSchedules reservationSchedules) {
        return new CancellingReservation(reservationSchedules);
    }

    @Bean
    public MakingReservationEffective makingReservationEffective() {
        return new MakingReservationEffective();
    }

    @Bean
    @Profile("local")
    public ReservationSchedules inMemoryReservationSchedules() {
        return new InMemoryReservationScheduleRepository(domainModelMapper, eventPublisher);
    }

}
