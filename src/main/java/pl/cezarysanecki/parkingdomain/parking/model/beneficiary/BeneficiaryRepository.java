package pl.cezarysanecki.parkingdomain.parking.model.beneficiary;

public interface BeneficiaryRepository {

  void save(BeneficiaryId beneficiaryId);

  boolean isPresent(BeneficiaryId beneficiaryId);

}
