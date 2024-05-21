package pl.cezarysanecki.parkingdomain.cleaning.infrastructure;

import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.cleaning.application.CallingExternalCleaningServicePolicy;
import pl.cezarysanecki.parkingdomain.cleaning.application.CountingReleasedOccupationsEventHandler;
import pl.cezarysanecki.parkingdomain.cleaning.application.ExternalCleaningService;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@RequiredArgsConstructor
public class CleaningConfig {

    @Bean
    CountingReleasedOccupationsEventHandler countingReleasedOccupationsEventHandler(
            CleaningRepository cleaningRepository
    ) {
        return new CountingReleasedOccupationsEventHandler(cleaningRepository);
    }

    @Bean
    CallingExternalCleaningServicePolicy callingExternalCleaningServicePolicy(
            CleaningRepository cleaningRepository,
            ExternalCleaningService externalCleaningService,
            @Value("${business.cleaning.numberOfDrivesAwayToConsiderParkingSpotDirty}") int numberOfDrivesAwayToConsiderParkingSpotDirty,
            @Value("${business.cleaning.numberOfDirtyParkingSpotsToCallExternalService}") int numberOfDirtyParkingSpotsToCallExternalService
    ) {
        return new CallingExternalCleaningServicePolicy(
                cleaningRepository,
                externalCleaningService,
                numberOfDrivesAwayToConsiderParkingSpotDirty,
                numberOfDirtyParkingSpotsToCallExternalService);
    }

    @Bean
    @Profile("!local")
    JobDetail callingExternalCleaningServicePolicyJob() {
        return JobBuilder.newJob()
                .storeDurably()
                .ofType(CallingExternalCleaningServicePolicyJob.class)
                .withIdentity("calling-external-cleaning-service-policy-job")
                .build();
    }

    @Bean
    @Profile("!local")
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

    @Bean
    @Profile("local")
    InMemoryCleaningRepository cleaningRepository() {
        return new InMemoryCleaningRepository();
    }

}
