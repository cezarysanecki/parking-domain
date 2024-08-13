package pl.cezarysanecki.parkingdomain.cleaning.infrastructure;

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
class CleaningInfrastructureConfig {

  @Bean
  JobDetail callingExternalCleaningServicePolicyJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(CallingExternalCleaningServicePolicyJob.class)
        .withIdentity("calling-external-cleaning-service-policy-job")
        .build();
  }

  @Bean
  Trigger callingExternalCleaningServicePolicyJobTrigger(
      JobDetail callingExternalCleaningServicePolicyJob,
      @Value("${job.callingExternalCleaningServicePolicyJob.cronExpression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("calling-external-cleaning-service-policy-job-trigger")
        .forJob(callingExternalCleaningServicePolicyJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

}
