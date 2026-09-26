package pl.cezarysanecki.parkingdomain.parking.infrastructure;

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
class ParkingInfrastructureConfig {

  @Bean
  JobDetail callingTowingServiceAfterClosingJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(CallingTowingServiceAfterClosingJob.class)
        .withIdentity("calling-towing-service-after-closing-job")
        .build();
  }

  @Bean
  Trigger callingTowingServiceAfterClosingJobTrigger(
      JobDetail callingTowingServiceAfterClosingJob,
      @Value("${job.calling-towing-service-after-closing-job.cron-expression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("calling-towing-service-after-closing-job-trigger")
        .forJob(callingTowingServiceAfterClosingJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

}
