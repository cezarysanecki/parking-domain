package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Reservation;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed;

@Slf4j
@RequiredArgsConstructor
public class ReservingParkingSpotEventHandler {

    private final ParkingSpotRepository parkingSpotRepository;

    @EventListener
    public void handle(ReservationRequestConfirmed event) {
        ParkingSpot parkingSpot = findParkingSpotBy(event.parkingSpotId());
        log.debug("reserving parking spot with id: {}", parkingSpot.getParkingSpotId());

        Try<Reservation> result = parkingSpot.reserveUsing(event.reservationRequest());

        result
                .onFailure(exception -> log.error("cannot reserve parking spot, reason: {}", exception.getMessage()))
                .onSuccess(occupation -> parkingSpotRepository.save(parkingSpot));
    }

    private ParkingSpot findParkingSpotBy(ParkingSpotId parkingSpotId) {
        return parkingSpotRepository.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
    }

}
