package com.decamanager.vehiculo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decamanager.common.ConflictoException;
import com.decamanager.common.RecursoNoEncontradoException;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehiculoRepository repository;

    @InjectMocks
    private VehiculoService service;

    @Test
    void crear_normalizaMatriculaYLaGuarda() {
        var request = new VehiculoRequest(" 1234abc ", "Tractora", null);
        when(repository.existsByMatricula("1234ABC")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = service.crear(request);

        assertThat(respuesta.matricula()).isEqualTo("1234ABC");
        verify(repository).save(any());
    }

    @Test
    void crear_siLaMatriculaYaExiste_lanzaConflicto() {
        var request = new VehiculoRequest("1234ABC", "Tractora", null);
        when(repository.existsByMatricula("1234ABC")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("1234ABC");
    }

    @Test
    void obtener_siNoExiste_lanzaRecursoNoEncontrado() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(999L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void darDeBaja_marcaInactivoSinBorrar() {
        var vehiculo = new Vehiculo("1234ABC", "Tractora", null);
        when(repository.findById(1L)).thenReturn(Optional.of(vehiculo));

        var respuesta = service.darDeBaja(1L);

        assertThat(respuesta.activo()).isFalse();
    }
}
