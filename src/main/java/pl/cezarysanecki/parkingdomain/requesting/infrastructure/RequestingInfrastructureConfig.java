package pl.cezarysanecki.parkingdomain.requesting.infrastructure;

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
class RequestingInfrastructureConfig {

  @Bean
  JobDetail makingRequestsValidJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(MakingRequestsValidJob.class)
        .withIdentity("making-requests-valid-job")
        .build();
  }

  @Bean
  Trigger makingRequestsValidJobTrigger(
      JobDetail makingRequestsValidJob,
      @Value("${job.making-requests-valid-job.cron-expression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("making-requests-valid-job-trigger")
        .forJob(makingRequestsValidJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

}
