package com.decamanager.transporte;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class FechasCoherentesValidatorTest {

    private final FechasCoherentesValidator validator = new FechasCoherentesValidator();

    @Test
    void esValido_cuandoFechaCargaEsAnteriorALaDescarga() {
        var request = requestConFechas(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2));

        assertThat(validator.isValid(request, null)).isTrue();
    }

    @Test
    void esValido_cuandoFechaCargaEsIgualALaDescarga() {
        var fecha = LocalDate.of(2026, 10, 1);
        var request = requestConFechas(fecha, fecha);

        assertThat(validator.isValid(request, null)).isTrue();
    }

    @Test
    void noEsValido_cuandoFechaCargaEsPosteriorALaDescarga() {
        var request = requestConFechas(LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 1));

        assertThat(validator.isValid(request, null)).isFalse();
    }

    @Test
    void esValido_cuandoAlgunaFechaEsNula() {
        var request = new TransporteRequest(
                1L, 2L, 3L, LocalDate.now(), "Madrid", null,
                "Barcelona", LocalDate.of(2026, 10, 2),
                "Textil", BigDecimal.TEN, "TM", null);

        assertThat(validator.isValid(request, null)).isTrue();
    }

    private TransporteRequest requestConFechas(LocalDate fechaCarga, LocalDate fechaDescarga) {
        return new TransporteRequest(
                1L, 2L, 3L, LocalDate.now(), "Madrid", fechaCarga,
                "Barcelona", fechaDescarga,
                "Textil", BigDecimal.TEN, "TM", null);
    }
}
