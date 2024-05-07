package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ParkingSpotFacade {

    private final ParkingSpotRepository parkingSpotRepository;

    public Try<OccupationId> occupy(ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
        ParkingSpot parkingSpot = findBy(parkingSpotId);
        Try<OccupationId> occupationId = parkingSpot.occupy(spotUnits);
        return occupationId.onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()));
    }

    public Try<OccupationId> occupy(ParkingSpotId parkingSpotId, ReservationId reservationId) {
        ParkingSpot parkingSpot = findBy(parkingSpotId);
        Try<OccupationId> occupationId = parkingSpot.occupy(reservationId);
        return occupationId.onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()));
    }

    public Try<OccupationId> occupyAll(ParkingSpotId parkingSpotId) {
        ParkingSpot parkingSpot = findBy(parkingSpotId);
        Try<OccupationId> occupationId = parkingSpot.occupyAll();
        return occupationId.onFailure(exception -> log.error("cannot occupy all parking spot, reason: {}", exception.getMessage()));
    }

    public Try<OccupationId> release(ParkingSpotId parkingSpotId, OccupationId occupationId) {
        ParkingSpot parkingSpot = findBy(parkingSpotId);
        Try<OccupationId> processedOccupationId = parkingSpot.release(occupationId);
        return processedOccupationId.onFailure(exception -> log.error("cannot release parking spot, reason: {}", exception.getMessage()));
    }

    private ParkingSpot findBy(ParkingSpotId parkingSpotId) {
        return parkingSpotRepository.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
    }

}
