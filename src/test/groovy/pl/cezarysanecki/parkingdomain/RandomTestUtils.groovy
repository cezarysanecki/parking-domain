package pl.cezarysanecki.parkingdomain

import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber

import java.util.concurrent.ThreadLocalRandom

final class RandomTestUtils {

  private static final String RANDOM_BASE = "0123456789"

  private RandomTestUtils() {}

  static PhoneNumber randomPhoneNumber() {
    def random = ThreadLocalRandom.current()
    def digits = (1..9).collect { RANDOM_BASE[random.nextInt(RANDOM_BASE.length())] }.join()
    return PhoneNumber.of(digits)
  }

}
