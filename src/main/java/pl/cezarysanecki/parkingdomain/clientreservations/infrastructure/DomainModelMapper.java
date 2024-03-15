package pl.cezarysanecki.parkingdomain.clientreservations.infrastructure;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservations;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ClientReservations map(ClientReservationsEntity entity) {
        return new ClientReservations(
                ClientId.of(entity.clientId),
                entity.numberOfReservations);
    }

}
