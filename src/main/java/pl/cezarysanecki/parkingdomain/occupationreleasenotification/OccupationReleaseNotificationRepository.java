package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.Instant;
import java.util.List;

interface OccupationReleaseNotificationRepository {

  void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity);

  void addOccupation(OccupationId occupationId, OccupantId occupantId, ParkingSpotId parkingSpotId, SpotUnits spotUnits);

  void removeOccupation(OccupationId occupationId);

  void saveReservation(ReservationId reservationId, ReservationOwnerId reservationOwnerId, ParkingSpotId parkingSpotId, Instant instant, SpotUnits spotUnits);

  void doneFor(Instant date);

  List<NotificationResolver> findFor(Instant date);
}
