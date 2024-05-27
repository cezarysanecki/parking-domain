package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestEvent.ReservationRequestConfirmed;

@Value
@AllArgsConstructor
public class ReservationRequest {

  @NonNull
  ReservationRequestId reservationRequestId;
  @NonNull
  ReservationRequesterId requesterId;
  @NonNull
  ReservationRequestsTimeSlotId timeSlotId;
  @NonNull
  SpotUnits spotUnits;

  public ReservationRequest(
      ReservationRequesterId requesterId,
      ReservationRequestsTimeSlotId timeSlotId,
      SpotUnits spotUnits
  ) {
    this.reservationRequestId = ReservationRequestId.newOne();
    this.requesterId = requesterId;
    this.timeSlotId = timeSlotId;
    this.spotUnits = spotUnits;
  }

  public ReservationRequestCancelled cancel() {
    return new ReservationRequestCancelled(this);
  }

  public ReservationRequestConfirmed confirm() {
    return new ReservationRequestConfirmed(this);
  }

}
