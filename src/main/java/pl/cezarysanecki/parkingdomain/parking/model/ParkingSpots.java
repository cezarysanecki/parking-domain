package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.model.parking.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parking.ReservedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.releasing.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

public interface ParkingSpots {

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    Option<OpenParkingSpot> findBy(VehicleSizeUnit vehicleSizeUnit);

    Option<ReservedParkingSpot> findBy(ReservationId reservationId);

    Option<OccupiedParkingSpot> findBy(VehicleId vehicleId);

    ParkingSpot publish(ParkingSpotEvent event);

}
