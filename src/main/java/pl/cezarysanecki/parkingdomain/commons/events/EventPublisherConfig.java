package pl.cezarysanecki.parkingdomain.commons.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EventPublisherConfig {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean
    EventPublisher eventPublisher() {
        return new JustForwardDomainEventPublisher(applicationEventPublisher);
    }

}
