package pl.cezarysanecki.parkingdomain.availability.application;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.availability.model.AssignedSpot;
import pl.cezarysanecki.parkingdomain.availability.model.Spots;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

@RequiredArgsConstructor
public class AssigningAvailableSpots {

    private final Spots spots;
    private final EventPublisher eventPublisher;

    public void assignTo(AssignmentCommand command) {
        VehicleId vehicleId = command.getVehicleId();
        VehicleType vehicleType = command.getVehicleType();

        spots.findAvailableFor(vehicleType)
                .or(() -> spots.findAvailable()
                        .map(free -> free.assignTo(vehicleType)))
                .ifPresentOrElse(
                        availableSpot -> eventPublisher.publish(new AssignedSpot(
                                availableSpot.getParkingSpotId(), vehicleId)),
                        () -> {
                            throw new IllegalStateException("cannot find available spot for " + vehicleType);
                        });
    }

}
