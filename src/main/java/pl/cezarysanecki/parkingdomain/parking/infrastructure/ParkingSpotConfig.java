package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cezarysanecki.parkingdomain.parking.application.CreatingBeneficiaryEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.CreatingParkingSpotEventHandler;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ProvidingUsageOfParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReleasingOccupation;
import pl.cezarysanecki.parkingdomain.parking.application.ReservingParkingSpotEventHandler;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationRepository;

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
      ReservationRepository reservationRepository
  ) {
    return new ReservingParkingSpotEventHandler(reservationRepository);
  }

  @Bean
  ProvidingUsageOfParkingSpot providingUsageOfParkingSpot(
      ParkingSpotRepository parkingSpotRepository
  ) {
    return new ProvidingUsageOfParkingSpot(parkingSpotRepository);
  }

}
