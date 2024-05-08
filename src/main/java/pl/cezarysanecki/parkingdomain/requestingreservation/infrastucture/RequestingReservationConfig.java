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
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.StoringReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@RequiredArgsConstructor
public class RequestingReservationConfig {

    private final EventPublisher eventPublisher;

    @Bean
    StoringReservationRequest storingReservationRequest(
            ReservationRequesterRepository reservationRequesterRepository,
            ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository
    ) {
        return new StoringReservationRequest(
                eventPublisher,
                reservationRequesterRepository,
                parkingSpotReservationRequestsRepository);
    }

    @Bean
    CancellingReservationRequest cancellingReservationRequest(
            ReservationRequesterRepository reservationRequesterRepository,
            ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository
    ) {
        return new CancellingReservationRequest(
                eventPublisher,
                reservationRequesterRepository,
                parkingSpotReservationRequestsRepository);
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
    InMemoryReservationRequesterRepository reservationRequesterRepository() {
        return new InMemoryReservationRequesterRepository();
    }

    @Bean
    @Profile("local")
    InMemoryParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository() {
        return new InMemoryParkingSpotReservationRequestsRepository();
    }

}
