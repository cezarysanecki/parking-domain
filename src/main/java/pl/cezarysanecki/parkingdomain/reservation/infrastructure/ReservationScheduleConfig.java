package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.application.CancellingReservation;
import pl.cezarysanecki.parkingdomain.reservation.application.MakingParkingSlotReservation;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSchedules;

@Slf4j
@Configuration
public class ReservationScheduleConfig {

    private final EventPublisher eventPublisher;
    private final DomainModelMapper domainModelMapper;

    public ReservationScheduleConfig(EventPublisher eventPublisher, DateProvider dateProvider) {
        this.eventPublisher = eventPublisher;
        this.domainModelMapper = new DomainModelMapper(dateProvider);
    }

    @Bean
    public MakingParkingSlotReservation makingParkingSlotReservation(ReservationSchedules reservationSchedules) {
        return new MakingParkingSlotReservation(reservationSchedules);
    }

    @Bean
    public CancellingReservation cancellingReservation(ReservationSchedules reservationSchedules) {
        return new CancellingReservation(reservationSchedules);
    }

    @Bean
    @Profile("local")
    public ReservationSchedules inMemoryReservationSchedules() {
        return new InMemoryReservationScheduleRepository(domainModelMapper, eventPublisher);
    }

}
