package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

public interface ReservationRequestEvent {

  record ReservationRequestCancelled(
      ReservationRequest reservationRequest
  ) implements ReservationRequestEvent {
  }

  record ReservationRequestConfirmed(
      ReservationRequest reservationRequest
  ) implements ReservationRequestEvent {
  }

}
