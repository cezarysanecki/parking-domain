package pl.cezarysanecki.parkingdomain.cleaning.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class CleaningController {

    private final CleaningViewRepository cleaningViewRepository;

    @GetMapping("/cleaning-service")
    ResponseEntity<CleaningViewRepository.CleaningView> cleaningService() {
        return ok(cleaningViewRepository.queryForCleaningView());
    }

}
