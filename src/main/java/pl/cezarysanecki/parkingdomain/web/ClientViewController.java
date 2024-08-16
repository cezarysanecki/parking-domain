package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.views.ViewCurrentStateOfClientRepository;

import java.util.UUID;

@RestController
@RequestMapping("/client/view")
@RequiredArgsConstructor
class ClientViewController {

  private final ViewCurrentStateOfClientRepository viewCurrentStateOfClientRepository;

  @GetMapping
  ResponseEntity queryAll() {
    return ResponseEntity.ok(viewCurrentStateOfClientRepository.queryAll());
  }

  @GetMapping("/{clientId}")
  ResponseEntity query(@PathVariable("clientId") UUID clientId) {
    return ResponseEntity.ok(viewCurrentStateOfClientRepository.queryFor(new ClientId(clientId)));
  }

}
