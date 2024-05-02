package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.CompletelyReleased;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.FullyOccupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupationFailed;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Occupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupiedEvents;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupiedEvents.events;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Released;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ReleasedEvents;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ReleasingFailed;

@Value
public class ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    @NonNull
    SpotUnits spaceLeft;
    @NonNull
    SpotUnits capacity;

    public Either<OccupationFailed, OccupiedEvents> occupy(SpotUnits spotUnits) {
        if (spaceLeft.isLessThan(spotUnits)) {
            return announceFailure(new OccupationFailed(parkingSpotId, "there is not enough space"));
        }

        Occupied occupied = new Occupied(parkingSpotId, spotUnits);
        if (spaceLeft.isEqualTo(spotUnits)) {
            return announceSuccess(events(parkingSpotId, occupied, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, occupied));
    }

    public Either<ReleasingFailed, ReleasedEvents> release(SpotUnits spotUnits) {
        if (capacity.isLessThan()) {
            return announceFailure(new ReleasingFailed(parkingSpotId, "is already empty"));
        }

        Released released = new Released(parkingSpotId, spotUnits);
        if (spotOccupation.) {
            return announceSuccess(ReleasedEvents.events(parkingSpotId, released, new CompletelyReleased(parkingSpotId)));
        }
        return announceSuccess(ReleasedEvents.events(parkingSpotId, released));
    }

}
