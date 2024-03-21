package pl.cezarysanecki.parkingdomain.reservation.effectiveness;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ReservationScheduleConfig {

    private final DateProvider dateProvider;
    private final EventPublisher eventPublisher;

    @Bean
    public MakingReservationEffective makingReservationEffective(ReservationToMakeEffectiveRepository reservationToMakeEffectiveRepository) {
        return new MakingReservationEffective(
                dateProvider,
                eventPublisher,
                reservationToMakeEffectiveRepository);
    }

    @Bean
    @Profile("local")
    public ReservationToMakeEffectiveRepository reservationToMakeEffectiveRepository() {
        return new ReservationToMakeEffectiveRepository.InMemoryReservationToMakeEffectiveRepository();
    }

}
