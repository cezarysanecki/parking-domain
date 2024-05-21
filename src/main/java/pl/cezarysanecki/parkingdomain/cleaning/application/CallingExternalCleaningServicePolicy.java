package pl.cezarysanecki.parkingdomain.cleaning.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CallingExternalCleaningServicePolicy {

    private final CleaningRepository cleaningRepository;
    private final ExternalCleaningService externalCleaningService;

    public void handleCleaningPolicy() {
        List<ParkingSpotId> parkingSpotIds = cleaningRepository.getAllRecordsWithCounterAbove(20);

        if (parkingSpotIds.size() > 2) {
            log.debug("At least {} parking spots need to be cleaned, calling external service");
            externalCleaningService.call();
            cleaningRepository.resetCountersFor(parkingSpotIds);
        } else {
            log.debug("Still not enough parking spots are dirty to call external service");
        }
    }

}
