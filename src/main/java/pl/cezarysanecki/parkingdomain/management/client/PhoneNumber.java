package pl.cezarysanecki.parkingdomain.management.client;

import lombok.NonNull;
import lombok.Value;
import org.springframework.util.ObjectUtils;

@Value
public class PhoneNumber {

  @NonNull
  String value;

  private PhoneNumber(String value) {
    if (ObjectUtils.isEmpty(value)) {
      throw new IllegalArgumentException("phone number cannot be empty");
    }
    this.value = value;
  }

  public static PhoneNumber of(String value) {
    return new PhoneNumber(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
