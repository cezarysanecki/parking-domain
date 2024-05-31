package pl.cezarysanecki.parkingdomain.parking.model.beneficiary;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;

public interface BeneficiaryRepository {

  void save(Beneficiary beneficiary);

  Option<Beneficiary> findBy(BeneficiaryId beneficiaryId);

  Option<Beneficiary> findBy(OccupationId occupationId);

  Option<Beneficiary> findBy(ReservationId reservationId);

}
