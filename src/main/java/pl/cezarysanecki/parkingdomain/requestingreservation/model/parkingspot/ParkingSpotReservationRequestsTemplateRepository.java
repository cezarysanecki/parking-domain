package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.collection.List;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

public interface ParkingSpotReservationRequestsTemplateRepository {

    void store(ParkingSpotReservationRequestsTemplate template);

    List<ParkingSpotReservationRequestsTemplate> findAll();

    record ParkingSpotReservationRequestsTemplate(
            ParkingSpotId parkingSpotId,
            ParkingSpotCategory parkingSpotCategory,
            ParkingSpotCapacity capacity) {

    }

}
