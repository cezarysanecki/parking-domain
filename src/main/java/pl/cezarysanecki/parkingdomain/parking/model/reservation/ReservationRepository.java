package pl.cezarysanecki.parkingdomain.parking.model.reservation;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

public interface ReservationRepository {

  void saveNew(ReservationId reservationId, ParkingSpotId parkingSpotId, BeneficiaryId beneficiaryId, SpotUnits spotUnits);

  Option<Reservation> findBy(ReservationId reservationId);

}
