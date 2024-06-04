package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.web.BeneficiaryViewRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class InMemoryBeneficiaryRepository implements
    BeneficiaryRepository,
    BeneficiaryViewRepository {

  static final Set<BeneficiaryId> DATABASE = new HashSet<>();

  @Override
  public List<BeneficiaryView> queryForAllBeneficiaries() {
    return DATABASE.stream()
        .map(beneficiaryId -> {
          List<UUID> occupations = InMemoryOccupationRepository.DATABASE.stream()
              .filter(entity -> entity.beneficiaryId.equals(beneficiaryId.getValue()))
              .map(entity -> entity.occupationId)
              .toList();
          List<UUID> reservations = InMemoryReservationRepository.DATABASE.stream()
              .filter(entity -> entity.beneficiaryId.equals(beneficiaryId.getValue()))
              .map(entity -> entity.reservationId)
              .toList();
          return new BeneficiaryView(beneficiaryId.getValue(), occupations, reservations);
        })
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
