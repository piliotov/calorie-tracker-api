package com.piliotov.calorietracker.nutrition;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MacrosTest {

    @Test
    void addsAndSubtractsComponentWise() {
        Macros a = new Macros(500, 30, 50, 20);
        Macros b = new Macros(200, 10, 20, 5);

        assertThat(a.plus(b)).isEqualTo(new Macros(700, 40, 70, 25));
        assertThat(a.minus(b)).isEqualTo(new Macros(300, 20, 30, 15));
    }

    @Test
    void scalesAllValues() {
        assertThat(new Macros(100, 10, 20, 5).scale(1.5)).isEqualTo(new Macros(150, 15, 30, 7.5));
    }

    @Test
    void roundsToOneDecimal() {
        assertThat(new Macros(123.456, 7.04, 8.96, 0.05).rounded())
                .isEqualTo(new Macros(123.5, 7.0, 9.0, 0.1));
    }
}
