package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.application.CreatingBeneficiaryEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.CreatingParkingSpotEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReservingParkingSpotEventHandler;
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
    CreatingParkingSpotEventHandler creatingParkingSpotEventHandler(
            ParkingSpotRepository parkingSpotRepository
    ) {
        return new CreatingParkingSpotEventHandler(parkingSpotRepository);
    }

    @Bean
    CreatingBeneficiaryEventHandler creatingBeneficiaryEventHandler(
            BeneficiaryRepository beneficiaryRepository
    ) {
        return new CreatingBeneficiaryEventHandler(beneficiaryRepository);
    }

    @Bean
    ReservingParkingSpotEventHandler reservingParkingSpotEventHandler(
            BeneficiaryRepository beneficiaryRepository,
            ParkingSpotRepository parkingSpotRepository
    ) {
        return new ReservingParkingSpotEventHandler(
                beneficiaryRepository,
                parkingSpotRepository);
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
