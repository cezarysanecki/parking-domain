package pl.cezarysanecki.parkingdomain.parkingspot.parking.model;

public interface ParkingSpot {

    default ParkingSpotId getParkingSpotId() {
        return getParkingSpotInformation().getParkingSpotId();
    }

    default ParkingSpotOccupation getParkingSpotOccupation() {
        return getParkingSpotInformation().getParkingSpotOccupation();
    }

    ParkingSpotInformation getParkingSpotInformation();

}
