package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationEvent;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.HashSet;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationEvent.ReservationAbandoned;

class InMemoryReservationRepository implements ReservationRepository {

  static final Set<ReservationEntity> DATABASE = new HashSet<>();

  @Override
  public void saveNew(ReservationId reservationId, ParkingSpotId parkingSpotId, BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
    DATABASE.add(new ReservationEntity(
        reservationId.getValue(),
        parkingSpotId.getValue(),
        beneficiaryId.getValue(),
        spotUnits.getValue()));
  }

  @Override
  public Option<Reservation> findBy(ReservationId reservationId) {
    return Option.ofOptional(DATABASE.stream()
            .filter(entity -> entity.reservationId.equals(reservationId.getValue()))
            .findFirst())
        .map(ReservationEntity::toDomain);
  }

  @Override
  public void publish(ReservationEvent event) {
    if (event instanceof ReservationAbandoned abandoned) {
      DATABASE.removeIf(entity -> entity.reservationId.equals(abandoned.reservationId().getValue()));
    }
  }

}
