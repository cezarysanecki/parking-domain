package pl.cezarysanecki.parkingdomain._global.jooqconverter;

import org.jooq.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateConverter implements Converter<LocalDateTime, LocalDate> {

  public static final Converter<LocalDateTime, LocalDate> CONVERTER = new LocalDateConverter();

  @Override
  public LocalDate from(LocalDateTime localDateTime) {
    return localDateTime.toLocalDate();
  }

  @Override
  public LocalDateTime to(LocalDate localDate) {
    return localDate.atStartOfDay();
  }

  @Override
  public Class<LocalDateTime> fromType() {
    return LocalDateTime.class;
  }

  @Override
  public Class<LocalDate> toType() {
    return LocalDate.class;
  }

}
