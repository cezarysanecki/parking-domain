package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.collection.List;

public interface ReservationRequestsTemplateRepository {

  void save(ReservationRequestsTemplate template);

  List<ReservationRequestsTemplate> findAll();

}
