package pl.cezarysanecki.parkingdomain.requesting.policies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CreatingTimeSlotForNextDayPolicy.CreatingTimeSlotsConfig.class)
public class CreatingTimeSlotForNextDayPolicy {

  private final DateProvider dateProvider;

  private final RequestingFacade requestingFacade;
  private final CreatingTimeSlotsConfig creatingTimeSlotsConfig;

  public void run() {
    LocalDate nextDay = dateProvider.nextDay();

    if (requestingFacade.wereRequestableParkingSpotsCreatedAt(nextDay)) {
      throw new IllegalStateException("requestable parking spots were created at + nextDay");
    }

    requestingFacade.createForAll(
        TimeSlot.create(nextDay, creatingTimeSlotsConfig.morningStartHour, creatingTimeSlotsConfig.morningEndHour)
    );
    requestingFacade.createForAll(
        TimeSlot.create(nextDay, creatingTimeSlotsConfig.eveningStartHour, creatingTimeSlotsConfig.eveningEndHour)
    );
  }

  @ConfigurationProperties(prefix = "business.requesting.creating-time-slots")
  public record CreatingTimeSlotsConfig(
      int morningStartHour,
      int morningEndHour,
      int eveningStartHour,
      int eveningEndHour
  ) {

  }

}
