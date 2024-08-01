package pl.cezarysanecki.parkingdomain.requesting;

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
