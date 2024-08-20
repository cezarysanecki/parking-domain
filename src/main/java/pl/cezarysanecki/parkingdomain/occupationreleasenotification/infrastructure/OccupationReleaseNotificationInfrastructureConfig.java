package pl.cezarysanecki.parkingdomain.occupationreleasenotification.infrastructure;

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
class OccupationReleaseNotificationInfrastructureConfig {

  @Bean
  JobDetail notifyingAboutReleasingJob() {
    return JobBuilder.newJob()
        .storeDurably()
        .ofType(NotifyingAboutReleasingJob.class)
        .withIdentity("notifying-about-releasing-job")
        .build();
  }

  @Bean
  Trigger notifyingAboutReleasingJobTrigger(
      JobDetail notifyingAboutReleasingJob,
      @Value("${job.notifying-about-releasing-job.cron-expression}") String cronExpression
  ) {
    return TriggerBuilder.newTrigger()
        .withIdentity("notifying-about-releasing-job-trigger")
        .forJob(notifyingAboutReleasingJob)
        .withSchedule(cronSchedule(cronExpression))
        .startNow()
        .build();
  }

}
