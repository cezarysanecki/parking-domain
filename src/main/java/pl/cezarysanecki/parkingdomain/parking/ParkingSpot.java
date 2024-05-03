package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;

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
    ParkingSpotOccupation occupation;

    public Either<OccupationFailed, OccupiedEvents> occupy(SpotUnits spotUnits) {
        if (occupation.cannotHandle(spotUnits)) {
            return announceFailure(new OccupationFailed(parkingSpotId, "there is not enough space"));
        }

        Occupied occupied = new Occupied(parkingSpotId, spotUnits);
        if (occupation.handle(spotUnits).isFull()) {
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
