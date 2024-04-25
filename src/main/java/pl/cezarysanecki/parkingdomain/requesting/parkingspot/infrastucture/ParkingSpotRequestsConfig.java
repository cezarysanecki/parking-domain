package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.CancellingParkingSpotRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.CreatingParkingSpotRequestsEventsHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.StoringParkingSpotRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotRequestsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    StoringParkingSpotRequestEventHandler storingParkingSpotRequestEventHandler(
            ParkingSpotRequestsRepository parkingSpotRequestsRepository
    ) {
        return new StoringParkingSpotRequestEventHandler(parkingSpotRequestsRepository);
    }

    @Bean
    CancellingParkingSpotRequestEventHandler cancellingParkingSpotRequestEventHandler(
            ParkingSpotRequestsRepository parkingSpotRequestsRepository
    ) {
        return new CancellingParkingSpotRequestEventHandler(parkingSpotRequestsRepository);
    }

    @Bean
    CreatingParkingSpotRequestsEventsHandler creatingParkingSpotRequestsEventsHandler(
            ParkingSpotRequestsRepository parkingSpotRequestsRepository
    ) {
        return new CreatingParkingSpotRequestsEventsHandler(parkingSpotRequestsRepository);
    }

    @Bean
    JobDetail makingRequestsValidJobDetail() {
        return JobBuilder.newJob()
                .storeDurably()
                .ofType(MakingRequestsValidJob.class)
                .withIdentity("making-requests-valid-job")
                .build();
    }

    @Bean
    Trigger makingRequestsValidJobTrigger(
            JobDetail makingRequestsValidJobDetail,
            @Value("${job.makingRequestsValidJob.cronExpression}") String cronExpression
    ) {
        return TriggerBuilder.newTrigger()
                .withIdentity("making-requests-valid-job-trigger")
                .forJob(makingRequestsValidJobDetail)
                .withSchedule(cronSchedule(cronExpression))
                .startNow()
                .build();
    }

    @Bean
    @Profile("local")
    InMemoryParkingSpotRequestsRepository parkingSpotRequestsRepository() {
        return new InMemoryParkingSpotRequestsRepository(eventPublisher);
    }

}
