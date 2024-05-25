package pl.cezarysanecki.parkingdomain.management.client;

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
  ClientType type;
  @NonNull
  PhoneNumber phoneNumber;

  Client(UUID clientId, ClientType type, String phoneNumber) {
    this(ClientId.of(clientId), type, PhoneNumber.of(phoneNumber));
  }

}
