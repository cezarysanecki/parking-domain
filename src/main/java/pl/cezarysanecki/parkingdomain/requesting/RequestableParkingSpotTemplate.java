package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

record RequestableParkingSpotTemplate(
    ParkingSpotId parkingSpotId,
    int numberOfSections
) {
}
