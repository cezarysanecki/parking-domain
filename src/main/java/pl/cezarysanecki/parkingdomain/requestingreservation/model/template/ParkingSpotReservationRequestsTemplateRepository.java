package pl.cezarysanecki.parkingdomain.requestingreservation.model.template;

import io.vavr.collection.List;

public interface ParkingSpotReservationRequestsTemplateRepository {

    void store(ParkingSpotReservationRequestsTemplate template);

    List<ParkingSpotReservationRequestsTemplate> findAll();

}
