package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class ExchangingReservationRequestsTimeSlotsJob implements Job {

  private final DateProvider dateProvider;
  private final ExchangingReservationRequestsTimeSlots exchangingReservationRequestsTimeSlots;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
    Instant date = dateProvider.tomorrowMidnight();
    if (exchangingReservationRequestsTimeSlots.reservationRequestsTimeSlotRepository.containsAny()) {
      ExchangingReservationRequestsTimeSlots.log.error("there are still reservation requests time slots, check it");
    } else {
      List<ExchangingReservationRequestsTimeSlots.ToCreate> results = exchangingReservationRequestsTimeSlots.reservationRequestsTemplateRepository.findAll()
          .flatMap(template -> Stream.of(
                  TimeSlot.createTimeSlot(date, 7, 17),
                  TimeSlot.createTimeSlot(date, 18, 23))
              .map(timeSlot -> new ExchangingReservationRequestsTimeSlots.ToCreate(
                  template.templateId(),
                  ReservationRequestsTimeSlotId.newOne(),
                  timeSlot
              )));
      results.forEach(result -> {
        ExchangingReservationRequestsTimeSlots.log.debug("saving reservation requests time slot with id {} and time slot {}", result.templateId(), result.timeSlot);

        exchangingReservationRequestsTimeSlots.reservationRequestsTimeSlotRepository.saveNew(
            result.timeSlotId,
            result.templateId,
            result.timeSlot);
      });
    }

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
