package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.util.List;

record Request(
    RequestId requestId,
    Requester requester,
    ParkingSpotId parkingSpotId,
    TimeSlot timeSlot,
    List<RequestableSection> sections) {
}
