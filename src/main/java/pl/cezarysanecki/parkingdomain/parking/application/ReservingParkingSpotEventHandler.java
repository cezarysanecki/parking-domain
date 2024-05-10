package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Reservation;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed;

@Slf4j
@RequiredArgsConstructor
public class ReservingParkingSpotEventHandler {

    private final BeneficiaryRepository beneficiaryRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @EventListener
    public void handle(ReservationRequestConfirmed event) {
        BeneficiaryId beneficiaryId = BeneficiaryId.of(event.validReservationRequest().getReservationRequesterId().getValue());

        Beneficiary beneficiary = findBeneficiaryBy(beneficiaryId);
        ParkingSpot parkingSpot = findParkingSpotBy(event.parkingSpotId());

        log.debug("reserving parking spot with id: {}", parkingSpot.getParkingSpotId());

        Try<Reservation> result = parkingSpot.reserveUsing(event.validReservationRequest())
                .flatMap(beneficiary::append);

        result
                .onFailure(exception -> log.error("cannot reserve parking spot, reason: {}", exception.getMessage()))
                .onSuccess(occupation -> parkingSpotRepository.save(parkingSpot));
    }

    private ParkingSpot findParkingSpotBy(ParkingSpotId parkingSpotId) {
        return parkingSpotRepository.findBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
    }

    private Beneficiary findBeneficiaryBy(BeneficiaryId beneficiaryId) {
        return beneficiaryRepository.findBy(beneficiaryId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
    }

}
