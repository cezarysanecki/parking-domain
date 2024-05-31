package pl.cezarysanecki.parkingdomain.parking.model.beneficiary;

import io.vavr.collection.HashSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeneficiaryFixture {

  public static Beneficiary emptyBeneficiary() {
    return new Beneficiary(
        BeneficiaryId.newOne(),
        HashSet.empty(),
        HashSet.empty(),
        Version.zero());
  }

  public static Beneficiary beneficiaryWithOccupation(OccupationId occupationId) {
    return new Beneficiary(
        BeneficiaryId.newOne(),
        HashSet.empty(),
        HashSet.of(occupationId),
        Version.zero());
  }

  public static Beneficiary beneficiaryWithReservation(ReservationId reservationId) {
    return new Beneficiary(
        BeneficiaryId.newOne(),
        HashSet.of(reservationId),
        HashSet.empty(),
        Version.zero());
  }

}
