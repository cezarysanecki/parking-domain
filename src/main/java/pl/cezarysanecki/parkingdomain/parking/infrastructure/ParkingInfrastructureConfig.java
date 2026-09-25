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
  JobDetail towingVehiclesAfterClosingJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(TowingVehiclesAfterClosingJob.class)
        .withIdentity("towing-vehicles-after-closing-job")
        .build();
  }

  @Bean
  Trigger towingVehiclesAfterClosingJobTrigger(
      JobDetail towingVehiclesAfterClosingJob,
      @Value("${job.towing-vehicles-after-closing-job.cron-expression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("towing-vehicles-after-closing-job-trigger")
        .forJob(towingVehiclesAfterClosingJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

}
