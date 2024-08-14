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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CreatingTimeSlotForNextDayPolicy.CreatingTimeSlotsConfig.class)
public class CreatingTimeSlotForNextDayPolicy {

  private final DateProvider dateProvider;
  private final RequestingFacade requestingFacade;
  private final CreatingTimeSlotsConfig creatingTimeSlotsConfig;

  public void run() {
    LocalDate currentDay = dateProvider.now().atZone(ZoneId.of("UTC")).toLocalDate();
    LocalDate nextDay = currentDay.plusDays(1);

    requestingFacade.createForAll(new TimeSlot(
        ZonedDateTime.of(nextDay, LocalTime.of(creatingTimeSlotsConfig.morningStartHour, 0), ZoneId.of("UTC")).toInstant(),
        ZonedDateTime.of(nextDay, LocalTime.of(creatingTimeSlotsConfig.morningEndHour, 0), ZoneId.of("UTC")).toInstant()
    ));
    requestingFacade.createForAll(new TimeSlot(
        ZonedDateTime.of(nextDay, LocalTime.of(creatingTimeSlotsConfig.eveningStartHour, 0), ZoneId.of("UTC")).toInstant(),
        ZonedDateTime.of(nextDay, LocalTime.of(creatingTimeSlotsConfig.eveningEndHour, 0), ZoneId.of("UTC")).toInstant()
    ));
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
