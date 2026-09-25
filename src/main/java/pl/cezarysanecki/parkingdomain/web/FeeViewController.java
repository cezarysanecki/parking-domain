package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.views.ViewFeesRepository;

import java.util.UUID;

@RestController
@RequestMapping("/fee/view")
@RequiredArgsConstructor
class FeeViewController {

  private final ViewFeesRepository viewFeesRepository;

  @GetMapping
  ResponseEntity queryAll() {
    return ResponseEntity.ok(viewFeesRepository.queryFees());
  }

  @GetMapping("/{clientId}")
  ResponseEntity query(@PathVariable("clientId") UUID clientId) {
    return ResponseEntity.ok(viewFeesRepository.queryFeesFor(new ClientId(clientId)));
  }

}
