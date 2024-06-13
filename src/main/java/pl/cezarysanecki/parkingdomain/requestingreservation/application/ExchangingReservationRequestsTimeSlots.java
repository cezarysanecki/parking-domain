package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
public class ExchangingReservationRequestsTimeSlots {

  private final ReservationRequestsTimeSlotRepository reservationRequestsTimeSlotRepository;
  private final ReservationRequestsTemplateRepository reservationRequestsTemplateRepository;

  public void exchangeTimeSlots(LocalDate date) {
    if (reservationRequestsTimeSlotRepository.containsAny()) {
      log.error("there are still reservation requests time slots, check it");
      return;
    }

    List<ToCreate> results = reservationRequestsTemplateRepository.findAll()
        .flatMap(template -> Stream.of(
                TimeSlot.createTimeSlotAtUTC(date, 7, 17),
                TimeSlot.createTimeSlotAtUTC(date, 18, 23))
            .map(timeSlot -> new ToCreate(
                template.templateId(),
                ReservationRequestsTimeSlotId.newOne(),
                timeSlot
            )));

    results.forEach(result -> {
      log.debug("saving reservation requests time slot with id {} and time slot {}", result.templateId(), result.timeSlot);

      reservationRequestsTimeSlotRepository.saveNew(
          result.timeSlotId,
          result.templateId,
          result.timeSlot);
    });
  }

  record ToCreate(
      ReservationRequestsTemplateId templateId,
      ReservationRequestsTimeSlotId timeSlotId,
      TimeSlot timeSlot
  ) {
  }

}
