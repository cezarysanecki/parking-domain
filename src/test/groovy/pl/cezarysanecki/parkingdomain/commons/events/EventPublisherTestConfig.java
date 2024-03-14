package pl.cezarysanecki.parkingdomain.commons.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EventPublisherTestConfig {

    @Bean
    @Primary
    EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new JustForwardDomainEventPublisher(applicationEventPublisher);
    }

}
