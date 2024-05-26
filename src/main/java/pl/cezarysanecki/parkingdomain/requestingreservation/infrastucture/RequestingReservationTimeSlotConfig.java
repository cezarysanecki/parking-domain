package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CreatingReservationRequesterEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CreatingReservationRequestsTemplatesEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.ExchangingReservationRequestsTimeSlots;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequestsValid;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.ReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotRepository;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@RequiredArgsConstructor
public class RequestingReservationTimeSlotConfig {

  private final DateProvider dateProvider;

  @Bean
  MakingReservationRequest storingReservationRequest(ReservationRequestsRepository reservationRequestsRepository) {
    return new MakingReservationRequest(reservationRequestsRepository);
  }

  @Bean
  CancellingReservationRequest cancellingReservationRequest(ReservationRequestsRepository reservationRequestsRepository) {
    return new CancellingReservationRequest(reservationRequestsRepository);
  }

  @Bean
  CreatingReservationRequestsTemplatesEventHandler creatingParkingSpotReservationRequestsEventHandler(
      ReservationRequestsTemplateRepository reservationRequestsTemplateRepository
  ) {
    return new CreatingReservationRequestsTemplatesEventHandler(reservationRequestsTemplateRepository);
  }

  @Bean
  CreatingReservationRequesterEventHandler creatingReservationRequesterEventHandler(
      ReservationRequesterRepository reservationRequesterRepository
  ) {
    return new CreatingReservationRequesterEventHandler(reservationRequesterRepository);
  }

  @Bean
  MakingReservationRequestsValid makingReservationRequestsValid(
      ReservationRequestsRepository reservationRequestsRepository,
      @Value("${business.reservationRequests.hoursToMakeValid}") int hoursToMakeReservationRequestValid
  ) {
    return new MakingReservationRequestsValid(
        reservationRequestsRepository,
        dateProvider,
        hoursToMakeReservationRequestValid);
  }

  @Bean
  ExchangingReservationRequestsTimeSlots creatingReservationRequestTimeSlots(
      ReservationRequestsTimeSlotRepository reservationRequestsTimeSlotRepository,
      ReservationRequestsTemplateRepository reservationRequestsTemplateRepository
  ) {
    return new ExchangingReservationRequestsTimeSlots(
        dateProvider,
        reservationRequestsTimeSlotRepository,
        reservationRequestsTemplateRepository);
  }

  @Bean
  @Profile("!local")
  JobDetail makingReservationRequestsValidJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(MakingReservationRequestsValidJob.class)
        .withIdentity("making-reservation-requests-valid-job")
        .build();
  }

  @Bean
  @Profile("!local")
  Trigger makingRequestsValidJobTrigger(
      JobDetail makingReservationRequestsValidJob,
      @Value("${job.makingRequestsValidJob.cronExpression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("making-reservation-requests-valid-job-trigger")
        .forJob(makingReservationRequestsValidJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

  @Bean
  @Profile("!local")
  JobDetail exchangingReservationRequestsTimeSlotsJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(ExchangingReservationRequestsTimeSlotsJob.class)
        .withIdentity("exchanging-reservation-requests-time-slots-job")
        .build();
  }

  @Bean
  @Profile("!local")
  Trigger creatingReservationRequestTimeSlotsJobTrigger(
      JobDetail exchangingReservationRequestsTimeSlotsJob,
      @Value("${job.callingExternalCleaningServicePolicyJob.cronExpression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("exchanging-reservation-requests-time-slots-job-trigger")
        .forJob(exchangingReservationRequestsTimeSlotsJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

}
