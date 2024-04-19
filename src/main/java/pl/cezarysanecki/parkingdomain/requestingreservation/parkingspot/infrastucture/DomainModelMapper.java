package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequests;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpotReservationRequests map(ParkingSpotReservationRequestsEntity entity) {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.of(entity.parkingSpotId),
                ParkingSpotOccupation.of(
                        entity.reservationRequests.stream()
                                .map(vehicleReservationEntity -> vehicleReservationEntity.size)
                                .reduce(0, Integer::sum),
                        entity.capacity),
                entity.reservationRequests.stream()
                        .map(vehicleReservationEntity -> vehicleReservationEntity.reservationId)
                        .map(ReservationId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
