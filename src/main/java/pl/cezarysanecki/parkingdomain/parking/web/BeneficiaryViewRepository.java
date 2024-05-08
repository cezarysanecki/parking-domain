package pl.cezarysanecki.parkingdomain.parking.web;

import java.util.List;
import java.util.UUID;

public interface BeneficiaryViewRepository {

    List<BeneficiaryView> queryForAllBeneficiaries();

    record BeneficiaryView(
            UUID beneficiaryId,
            List<UUID> occupations,
            List<UUID> reservations
    ) {
    }

}
