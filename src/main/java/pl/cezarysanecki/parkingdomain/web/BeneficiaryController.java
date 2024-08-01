package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class BeneficiaryController {

  private final BeneficiaryViewRepository beneficiaryViewRepository;

  @GetMapping("/beneficiaries")
  ResponseEntity<List<BeneficiaryViewRepository.BeneficiaryView>> beneficiaries() {
    return ok(beneficiaryViewRepository.queryForAllBeneficiaries());
  }

}
