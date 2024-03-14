package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.LocalDateTime;

public interface DateProvider {

    LocalDateTime currentLocalDateTime();

}
