package pl.cezarysanecki.parkingdomain.clientreservations.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ClientReservationsEntity {

    @Id
    Long id;
    UUID clientId;
    int numberOfReservations;

    ClientReservationsEntity(UUID clientId) {
        this.clientId = clientId;
        this.numberOfReservations = 0;
    }

    void handle(ClientReservationsEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ReservationRequestCreated.class)), this::handle),
                Case($(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationsEntity handle(ReservationRequestCreated reservationRequestCreated) {
        this.numberOfReservations++;
        return this;
    }

    private ClientReservationsEntity handle(ReservationRequestCancelled reservationRequestCancelled) {
        this.numberOfReservations--;
        return this;
    }

}
