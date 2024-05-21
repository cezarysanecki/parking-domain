package pl.cezarysanecki.parkingdomain.cleaning.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CallingExternalCleaningServicePolicy {

    private final CleaningRepository cleaningRepository;
    private final ExternalCleaningService externalCleaningService;
    private final int numberOfDrivesAwayToConsiderParkingSpotDirty;
    private final int numberOfDirtyParkingSpotsToCallExternalService;

    public Result handleCleaningPolicy() {
        List<ParkingSpotId> parkingSpotIds = cleaningRepository.getAllRecordsWithCounterAbove(numberOfDrivesAwayToConsiderParkingSpotDirty);

        if (parkingSpotIds.size() >= numberOfDirtyParkingSpotsToCallExternalService) {
            log.debug("At least {} parking spots need to be cleaned, calling external service", parkingSpotIds.size());
            externalCleaningService.call();
            cleaningRepository.resetCountersFor(parkingSpotIds);
            return Result.Success;
        } else {
            log.debug("Still not enough parking spots are dirty to call external service");
            return Result.Rejection;
        }
    }

}
