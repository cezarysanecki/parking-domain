package pl.cezarysanecki.parkingdomain.parking.web;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class ParkingSpotController {

    private final ParkingSpotViewRepository parkingSpotViewRepository;
    private final OccupyingParkingSpot occupyingParkingSpot;
    private final ReleasingParkingSpot releasingParkingSpot;

    @GetMapping("/parking-spots/available")
    ResponseEntity<List<ParkingSpotViewRepository.CapacityView>> availableParkingSpots() {
        return ok(parkingSpotViewRepository.queryForAllAvailableParkingSpots());
    }

    @PostMapping("/parking-spot/occupy")
    ResponseEntity occupy(@RequestBody OccupyParkingSpotRequest request) {
        Try<Occupation> result = occupyingParkingSpot.occupy(
                BeneficiaryId.of(request.beneficiaryId),
                ParkingSpotId.of(request.parkingSpotId),
                SpotUnits.of(request.units));

        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/parking-spot/occupy-reserved")
    ResponseEntity occupyReserved(@RequestBody OccupyReservedParkingSpotRequest request) {
        Try<Occupation> result = occupyingParkingSpot.occupy(
                ReservationId.of(request.reservationId));

        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/parking-spot/occupy-whole")
    ResponseEntity occupyWhole(@RequestBody OccupyWholeParkingSpotRequest request) {
        Try<Occupation> result = occupyingParkingSpot.occupyWhole(
                BeneficiaryId.of(request.beneficiaryId),
                ParkingSpotId.of(request.parkingSpotId));

        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/parking-spot/release")
    ResponseEntity release(@RequestBody ReleaseParkingSpotRequest request) {
        Try<Occupation> result = releasingParkingSpot.release(
                OccupationId.of(request.occupationId));

        return result
                .map(success -> ResponseEntity.ok().build())
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    record OccupyParkingSpotRequest(
            UUID beneficiaryId,
            UUID parkingSpotId,
            int units) {
    }

    record OccupyReservedParkingSpotRequest(
            UUID reservationId) {
    }

    record OccupyWholeParkingSpotRequest(
            UUID beneficiaryId,
            UUID parkingSpotId) {
    }

    record ReleaseParkingSpotRequest(
            UUID occupationId) {
    }

}
