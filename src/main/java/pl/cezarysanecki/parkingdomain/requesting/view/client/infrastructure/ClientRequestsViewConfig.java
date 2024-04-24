package pl.cezarysanecki.parkingdomain.requesting.view.client.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.requesting.view.client.model.ClientRequestsViews;

@Configuration
@RequiredArgsConstructor
public class ClientRequestsViewConfig {

    @Bean
    @Profile("local")
    InMemoryClientRequestsViewRepository clientRequestsViews() {
        return new InMemoryClientRequestsViewRepository();
    }

}
