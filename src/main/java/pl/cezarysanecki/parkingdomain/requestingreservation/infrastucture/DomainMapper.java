package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.List;

import static pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.InMemoryParkingSpotReservationRequestsRepository.ReservationRequestsEntity;
import static pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.InMemoryParkingSpotReservationRequestsRepository.ReservationRequestsEntity.CurrentRequestEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainMapper {

    static ParkingSpotReservationRequests map(ReservationRequestsEntity entity) {
        return new ParkingSpotReservationRequests(
                ParkingSpotId.of(entity.parkingSpotId),
                ParkingSpotTimeSlotId.of(entity.parkingSpotTimeSlotId),
                ParkingSpotCapacity.of(entity.capacity),
                map(entity.currentRequests),
                new Version(entity.version));
    }

    private static Map<ReservationRequestId, ReservationRequest> map(List<CurrentRequestEntity> entities) {
        return HashMap.ofEntries(
                Array.ofAll(entities.stream())
                        .map(entity -> Map.entry(ReservationRequestId.of(entity.reservationRequestId), new ReservationRequest(
                                        ReservationRequesterId.of(entity.requesterId),
                                        ReservationRequestId.of(entity.reservationRequestId),
                                        SpotUnits.of(entity.units)
                                ))
                        ));
    }

}
