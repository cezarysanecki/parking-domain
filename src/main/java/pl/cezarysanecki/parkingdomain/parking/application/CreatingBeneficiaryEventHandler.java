package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.ClientRegistered;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;

@Slf4j
@RequiredArgsConstructor
public class CreatingBeneficiaryEventHandler {

  private final BeneficiaryRepository beneficiaryRepository;

  @EventListener
  public void handle(ClientRegistered event) {
    BeneficiaryId beneficiaryId = BeneficiaryId.of(event.clientId().getValue());
    log.debug("storing beneficiary with id: {}", beneficiaryId);
    beneficiaryRepository.save(beneficiaryId);
  }

}
