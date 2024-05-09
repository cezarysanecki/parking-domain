package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.ClientRegistered;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;

@Slf4j
@RequiredArgsConstructor
public class StoringBeneficiaryEventHandler {

    private final BeneficiaryRepository beneficiaryRepository;

    @EventListener
    public void handle(ClientRegistered event) {
        Beneficiary beneficiary = Beneficiary.newOne(BeneficiaryId.of(event.clientId().getValue()));
        log.debug("storing beneficiary with id: {}", beneficiary.getBeneficiaryId());
        beneficiaryRepository.save(beneficiary);
    }

}
