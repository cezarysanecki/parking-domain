package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.cezarysanecki.parkingdomain.parking.application.CreatingBeneficiaryEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.CreatingParkingSpotEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingReservedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReleasingOccupation;
import pl.cezarysanecki.parkingdomain.parking.application.ReservingParkingSpotEventHandler;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationRepository;

@Import(LocalParkingSpotConfig.class)
@Configuration
@RequiredArgsConstructor
public class ParkingSpotConfig {

  @Bean
  OccupyingParkingSpot occupyingParkingSpot(
      BeneficiaryRepository beneficiaryRepository,
      ParkingSpotRepository parkingSpotRepository
  ) {
    return new OccupyingParkingSpot(
        beneficiaryRepository,
        parkingSpotRepository);
  }

  @Bean
  OccupyingReservedParkingSpot occupyingReservedParkingSpot(
      ParkingSpotRepository parkingSpotRepository
  ) {
    return new OccupyingReservedParkingSpot(parkingSpotRepository);
  }

  @Bean
  ReleasingOccupation releasingParkingSpot(
      OccupationRepository occupationRepository
  ) {
    return new ReleasingOccupation(occupationRepository);
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
      OccupationRepository occupationRepository,
      ReservationRepository reservationRepository
  ) {
    return new ReservingParkingSpotEventHandler(
        occupationRepository,
        reservationRepository);
  }

  @Bean
  ProvidingUsageOfParkingSpot providingUsageOfParkingSpot(
      ParkingSpotRepository parkingSpotRepository
  ) {
    return new ProvidingUsageOfParkingSpot(parkingSpotRepository);
  }

}
