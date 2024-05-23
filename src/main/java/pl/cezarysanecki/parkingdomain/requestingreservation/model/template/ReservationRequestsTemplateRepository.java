package pl.cezarysanecki.parkingdomain.requestingreservation.model.template;

import io.vavr.collection.List;

public interface ReservationRequestsTemplateRepository {

    void save(ReservationRequestsTemplate template);

    List<ReservationRequestsTemplate> findAll();

}
