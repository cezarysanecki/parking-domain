package pl.cezarysanecki.parkingdomain.shared.occupation;

public record SpotUnits(int value) {

  public SpotUnits {
    if (value <= 0) throw new IllegalArgumentException("spot units cannot be negative");
    if (!isPowerOfTwo(value)) throw new IllegalArgumentException("spot units must be a power of two");
  }

  private static boolean isPowerOfTwo(int value) {
    if (value < 1) return false;
    while (value > 1) {
      if (value % 2 != 0) return false;
      value /= 2;
    }
    return true;
  }

}
