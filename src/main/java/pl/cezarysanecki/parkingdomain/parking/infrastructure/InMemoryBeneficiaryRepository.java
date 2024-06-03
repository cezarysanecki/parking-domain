package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.web.BeneficiaryViewRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryBeneficiaryRepository implements
    BeneficiaryRepository,
    BeneficiaryViewRepository {

  static final Set<BeneficiaryId> DATABASE = new HashSet<>();

  @Override
  public List<BeneficiaryView> queryForAllBeneficiaries() {
    return DATABASE.values()
        .stream()
        .map(beneficiary -> new BeneficiaryView(
            beneficiary.getBeneficiaryId().getValue(),
            beneficiary.getOccupations().map(OccupationId::getValue).toJavaList(),
            beneficiary.getReservations().map(ReservationId::getValue).toJavaList()
        ))
        .toList();
  }

  @Override
  public void save(BeneficiaryId beneficiaryId) {
    DATABASE.add(beneficiaryId);
  }

  @Override
  public boolean isPresent(BeneficiaryId beneficiaryId) {
    return DATABASE.contains(beneficiaryId);
  }
}
