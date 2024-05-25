package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.web.BeneficiaryViewRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryBeneficiaryRepository implements
    BeneficiaryRepository,
    BeneficiaryViewRepository {

  static final Map<BeneficiaryId, Beneficiary> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void save(Beneficiary beneficiary) {
    DATABASE.put(beneficiary.getBeneficiaryId(), beneficiary);
  }

  @Override
  public Option<Beneficiary> findBy(BeneficiaryId beneficiaryId) {
    return Option.of(DATABASE.get(beneficiaryId));
  }

  @Override
  public Option<Beneficiary> findBy(OccupationId occupationId) {
    return Option.ofOptional(
        DATABASE.values()
            .stream()
            .filter(beneficiary -> beneficiary.getOccupations().contains(occupationId))
            .findFirst());
  }

  @Override
  public Option<Beneficiary> findBy(ReservationId reservationId) {
    return Option.ofOptional(
        DATABASE.values()
            .stream()
            .filter(beneficiary -> beneficiary.getReservations().contains(reservationId))
            .findFirst());
  }

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
}
