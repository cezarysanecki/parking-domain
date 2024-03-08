package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.Objects;

public class Occupation {

    private final int numerator;
    private final int denominator;

    private Occupation(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Occupation cannot be indeterminate");
        }
        if (numerator == 0) {
            this.numerator = 0;
            this.denominator = 1;
        } else {
            if (numerator > 0) {
                if (denominator < 0) {
                    throw new IllegalArgumentException("Occupation cannot be negative");
                }
                if (numerator > denominator) {
                    throw new IllegalArgumentException("Occupation cannot be greater then one unit");
                }
            } else {
                if (denominator > 0) {
                    throw new IllegalArgumentException("Occupation cannot be negative");
                }
                if (numerator < denominator) {
                    throw new IllegalArgumentException("Occupation cannot be greater then one unit");
                }
            }
            this.numerator = numerator;
            this.denominator = denominator;
        }
    }

    public static Occupation empty() {
        return new Occupation(0, 1);
    }

    public static Occupation full() {
        return new Occupation(1, 1);
    }

    public static Occupation of(int numerator, int denominator) {
        return new Occupation(numerator, denominator);
    }

    public Occupation add(Occupation occupation) {
        int leftNumerator = numerator * occupation.denominator;
        int rightNumerator = occupation.numerator * denominator;

        int newDenominator = denominator * occupation.denominator;

        return new Occupation(leftNumerator + rightNumerator, newDenominator);
    }

    public Occupation subtract(Occupation occupation) {
        int leftNumerator = numerator * occupation.denominator;
        int rightNumerator = occupation.numerator * denominator;

        int newDenominator = denominator * occupation.denominator;

        return new Occupation(leftNumerator - rightNumerator, newDenominator);
    }

    public boolean isFull() {
        return numerator == denominator;
    }

    public boolean isEmpty() {
        return numerator == 0;
    }

    public String percentage() {
        return String.format("%.2f", 100.0 * numerator / denominator) + "%";
    }

    @Override
    public String toString() {
        return percentage();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Occupation occupation = (Occupation) object;
        return subtract(occupation).isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

}
