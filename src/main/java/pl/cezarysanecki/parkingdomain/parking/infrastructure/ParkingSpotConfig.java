package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReservingParkingSpotEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.StoringBeneficiaryEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.StoringParkingSpotEventHandler;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotConfig {

    private final EventPublisher eventPublisher;

    @Bean
    OccupyingParkingSpot occupyingParkingSpot(
            BeneficiaryRepository beneficiaryRepository,
            ParkingSpotRepository parkingSpotRepository
    ) {
        return new OccupyingParkingSpot(
                eventPublisher,
                beneficiaryRepository,
                parkingSpotRepository);
    }

    @Bean
    ReleasingParkingSpot releasingParkingSpot(
            BeneficiaryRepository beneficiaryRepository,
            ParkingSpotRepository parkingSpotRepository
    ) {
        return new ReleasingParkingSpot(
                eventPublisher,
                beneficiaryRepository,
                parkingSpotRepository);
    }

    @Bean
    StoringParkingSpotEventHandler storingParkingSpotEventHandler(
            ParkingSpotRepository parkingSpotRepository
    ) {
        return new StoringParkingSpotEventHandler(parkingSpotRepository);
    }

    @Bean
    StoringBeneficiaryEventHandler storingBeneficiaryEventHandler(
            BeneficiaryRepository beneficiaryRepository
    ) {
        return new StoringBeneficiaryEventHandler(beneficiaryRepository);
    }

    @Bean
    ReservingParkingSpotEventHandler reservingParkingSpotEventHandler(
            ParkingSpotRepository parkingSpotRepository
    ) {
        return new ReservingParkingSpotEventHandler(parkingSpotRepository);
    }

    @Bean
    @Profile("local")
    InMemoryBeneficiaryRepository beneficiaryRepository() {
        return new InMemoryBeneficiaryRepository();
    }

    @Bean
    @Profile("local")
    InMemoryParkingSpotRepository parkingSpotRepository() {
        return new InMemoryParkingSpotRepository();
    }

}
