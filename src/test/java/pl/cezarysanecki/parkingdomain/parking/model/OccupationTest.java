package pl.cezarysanecki.parkingdomain.parking.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OccupationTest {

    @ParameterizedTest
    @CsvSource({"1,-1", "-1,1"})
    void occupation_cannot_be_negative(int numerator, int denominator) {
        assertThrows(IllegalArgumentException.class, () -> Occupation.of(numerator, denominator));
    }

    @ParameterizedTest
    @CsvSource({"2,1", "-7,-6"})
    void occupation_cannot_be_more_than_one_unit(int numerator, int denominator) {
        assertThrows(IllegalArgumentException.class, () -> Occupation.of(numerator, denominator));
    }

    @ParameterizedTest
    @CsvSource({"6,6", "-32,-32"})
    void create_full_occupation_using_fractal(int numerator, int denominator) {
        Occupation occupation = Occupation.of(numerator, denominator);

        assertTrue(occupation.isFull());
    }

    @Test
    void create_full_occupation() {
        Occupation occupation = Occupation.full();

        assertTrue(occupation.isFull());
    }

    @Test
    void create_empty_occupation() {
        Occupation occupation = Occupation.empty();

        assertTrue(occupation.isEmpty());
    }

    @Test
    void adding_split_unit_creates_one_unit_of_occupation() {
        Occupation occupationOneThird = Occupation.of(1, 3);
        Occupation occupationTwoSixth = Occupation.of(2, 6);
        Occupation occupationThreeNinth = Occupation.of(3, 9);

        Occupation result = Occupation.empty()
                .add(occupationOneThird)
                .add(occupationTwoSixth)
                .add(occupationThreeNinth);

        assertTrue(result.isFull());
    }

    @Test
    void subtracting_from_full_occupation_split_unit_make_occupation_empty() {
        Occupation occupationOneThird = Occupation.of(1, 3);
        Occupation occupationTwoSixth = Occupation.of(2, 6);
        Occupation occupationThreeNinth = Occupation.of(3, 9);

        Occupation result = Occupation.full()
                .subtract(occupationOneThird)
                .subtract(occupationTwoSixth)
                .subtract(occupationThreeNinth);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @CsvSource(value = {"1;3;33,33%", "1;4;25,00%", "3;7;42,86%"}, delimiter = ';')
    void represent_occupation_as_percentage(int numerator, int denominator, String expectedPercentage) {
        Occupation occupationOneThird = Occupation.of(numerator, denominator);

        assertEquals(expectedPercentage, occupationOneThird.percentage());
    }

}
