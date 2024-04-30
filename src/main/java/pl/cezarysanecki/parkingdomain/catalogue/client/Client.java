package pl.cezarysanecki.parkingdomain.catalogue.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class Client {

    @NonNull
    ClientId clientId;
    @NonNull
    PhoneNumber phoneNumber;

    Client(UUID clientId, String phoneNumber) {
        this(ClientId.of(clientId), PhoneNumber.of(phoneNumber));
    }

}
