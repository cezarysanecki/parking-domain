package pl.cezarysanecki.parkingdomain.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.requesting.policies.CreatingTimeSlotsForNextDayPolicy;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/requesting")
@RequiredArgsConstructor
class RequestingController {

  private final RequestingFacade requestingFacade;
  private final CreatingTimeSlotsForNextDayPolicy creatingTimeSlotsForNextDayPolicy;

  @PostMapping("/add-all")
  ResponseEntity createTimeSlots() {
    creatingTimeSlotsForNextDayPolicy.run();
    return ResponseEntity.ok().build();
  }

  @PostMapping("/request")
  ResponseEntity request(@RequestBody MakeRequestRequest request) {
    var result = requestingFacade.request(
        new RequesterId(request.requesterId),
        new ParkingSpotId(request.parkingSpotId),
        new TimeSlot(
            request.from.atZone(ZoneId.systemDefault()).toInstant(),
            request.to.atZone(ZoneId.systemDefault()).toInstant()
        ),
        new SpotUnits(request.spotUnits)
    );
    return result
        .map(RequestId::toString)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @DeleteMapping("/cancel")
  ResponseEntity cancelRequest(@RequestBody CancelRequestRequest request) {
    boolean result = requestingFacade.cancel(
        new RequestId(request.requestId)
    );
    return result ? ResponseEntity.ok().build() : ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
  }

  record MakeRequestRequest(
      UUID requesterId,
      UUID parkingSpotId,
      LocalDateTime from,
      LocalDateTime to,
      int spotUnits
  ) {
  }

  record CancelRequestRequest(
      UUID requestId
  ) {
  }

}
