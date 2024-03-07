package pl.cezarysanecki.parkingdomain.availability.application;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.availability.model.AssignedAvailableSpot;
import pl.cezarysanecki.parkingdomain.availability.model.AvailableSpots;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

@RequiredArgsConstructor
public class AssigningAvailableSpots {

    private final AvailableSpots availableSpots;
    private final EventPublisher eventPublisher;

    public void assignTo(AssignmentCommand command) {
        VehicleId vehicleId = command.getVehicleId();
        VehicleType vehicleType = command.getVehicleType();

        availableSpots.findAvailableFor(vehicleType)
                .or(availableSpots::findAvailable)
                .ifPresentOrElse(
                        availableSpot -> eventPublisher.publish(new AssignedAvailableSpot(
                                availableSpot.getParkingSpotId(), vehicleId)),
                        () -> {
                            throw new IllegalStateException("cannot find available spot for " + vehicleType);
                        });
    }

}
