package pl.cezarysanecki.parkingdomain.parkingspot.model;

public interface ParkingSpot {

    default ParkingSpotId getParkingSpotId() {
        return getParkingSpotInformation().getParkingSpotId();
    }

    default ParkingSpotOccupation getParkingSpotOccupation() {
        return getParkingSpotInformation().getParkingSpotOccupation();
    }

    ParkingSpotInformation getParkingSpotInformation();

}
