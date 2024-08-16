package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Profile("!local")
@Configuration
class ReservationInfrastructureConfig {

  @Bean
  JobDetail activatingReservationsJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(ActivatingReservationsJob.class)
        .withIdentity("activating-reservations-job")
        .build();
  }

  @Bean
  Trigger activatingReservationsJobTrigger(
      JobDetail activatingReservationsJob,
      @Value("${job.activating-reservations-job.cron-expression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("activating-reservations-job-trigger")
        .forJob(activatingReservationsJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

  @Bean
  JobDetail removingNotUsedReservationsJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(RemovingNotUsedReservationsJob.class)
        .withIdentity("removing-not-used-reservations-job")
        .build();
  }

  @Bean
  Trigger removingNotUsedReservationsJobTrigger(
      JobDetail removingNotUsedReservationsJob,
      @Value("${job.removing-not-used-reservations-job.cron-expression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("removing-not-used-reservations-job-trigger")
        .forJob(removingNotUsedReservationsJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

}
