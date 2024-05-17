package pl.cezarysanecki.parkingdomain.cleaning.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.cleaning.application.CountingReleasedOccupationsEventHandler;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;

@Configuration
@RequiredArgsConstructor
public class CleaningConfig {

    @Bean
    CountingReleasedOccupationsEventHandler requestingCleaning(
            CleaningRepository cleaningRepository
    ) {
        return new CountingReleasedOccupationsEventHandler(cleaningRepository);
    }

    @Bean
    @Profile("local")
    InMemoryCleaningRepository cleaningRepository() {
        return new InMemoryCleaningRepository();
    }

}
