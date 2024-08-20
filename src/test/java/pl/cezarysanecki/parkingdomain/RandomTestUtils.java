package pl.cezarysanecki.parkingdomain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomTestUtils {

  private static final String RANDOM_BASE = "0123456789";
  private static final Random RANDOM = new Random();

  public static PhoneNumber randomPhoneNumber() {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < 9; i++) {
      int index = RANDOM.nextInt(RANDOM_BASE.length());
      builder.append(RANDOM_BASE.charAt(index));
    }

    return PhoneNumber.of(builder.toString());
  }

}
