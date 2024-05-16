package pl.cezarysanecki.parkingdomain.cleaning.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Occupation;

@RequiredArgsConstructor
public class RequestingCleaning {

    private static final BeneficiaryId CLEANING_SERVICE = BeneficiaryId.newOne();

    private OccupyingParkingSpot occupyingParkingSpot;

    public void requestCleaning(ParkingSpotId parkingSpotId) {
        Try<Occupation> result = occupyingParkingSpot.occupyWhole(CLEANING_SERVICE, parkingSpotId);
    }

}
