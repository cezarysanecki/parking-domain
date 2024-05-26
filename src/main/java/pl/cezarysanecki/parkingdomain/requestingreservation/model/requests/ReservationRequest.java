package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestEvent.ReservationRequestConfirmed;

@Value
public class ReservationRequest {

  @NonNull
  ReservationRequestId reservationRequestId;
  @NonNull
  ReservationRequesterId reservationRequesterId;
  @NonNull
  ReservationRequestsTimeSlotId reservationRequestsTimeSlotId;
  @NonNull
  SpotUnits spotUnits;

  public ReservationRequest(
      ReservationRequesterId reservationRequesterId,
      ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
      SpotUnits spotUnits
  ) {
    this.reservationRequestId = ReservationRequestId.newOne();
    this.reservationRequesterId = reservationRequesterId;
    this.reservationRequestsTimeSlotId = reservationRequestsTimeSlotId;
    this.spotUnits = spotUnits;
  }

  public ReservationRequestCancelled cancel() {
    return new ReservationRequestCancelled(this);
  }

  public ReservationRequestConfirmed confirm() {
    return new ReservationRequestConfirmed(this);
  }

}
