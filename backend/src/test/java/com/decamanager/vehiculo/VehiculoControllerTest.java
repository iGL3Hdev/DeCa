package com.decamanager.vehiculo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.decamanager.common.ConflictoException;
import com.decamanager.common.RecursoNoEncontradoException;

@WebMvcTest(VehiculoController.class)
class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehiculoService service;

    @Test
    void listar_devuelveLaListaDelServicio() throws Exception {
        var vehiculo = new VehiculoResponse(1L, "1234ABC", "Tractora", null, true);
        when(service.listar(null)).thenReturn(List.of(vehiculo));

        mockMvc.perform(get("/api/vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricula").value("1234ABC"));
    }

    @Test
    void crear_conDatosValidos_devuelve201() throws Exception {
        var respuesta = new VehiculoResponse(1L, "1234ABC", "Tractora", null, true);
        when(service.crear(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"matricula":"1234ABC","tipo":"Tractora"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("1234ABC"));
    }

    @Test
    void crear_conMatriculaVacia_devuelve400() throws Exception {
        mockMvc.perform(post("/api/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"matricula":"","tipo":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.matricula").exists());
    }

    @Test
    void crear_conMatriculaDuplicada_devuelve409() throws Exception {
        when(service.crear(any())).thenThrow(new ConflictoException("Ya existe un vehículo con la matrícula 1234ABC"));

        mockMvc.perform(post("/api/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"matricula":"1234ABC","tipo":"Tractora"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void obtener_siNoExiste_devuelve404() throws Exception {
        when(service.obtener(anyLong())).thenThrow(new RecursoNoEncontradoException("No existe el vehículo con id 999"));

        mockMvc.perform(get("/api/vehiculos/999"))
                .andExpect(status().isNotFound());
    }
}

