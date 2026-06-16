package tuti.desi.presentacion.facturas;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.FacturaService;
import tuti.desi.servicios.PersonaService;

/**
 * Tests de integración del controller usando @WebMvcTest.
 * Solo levanta la capa web (Thymeleaf + MVC), sin Spring Data ni base de datos.
 */
@WebMvcTest(FacturasEditarController.class)
class FacturasEditarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacturaService facturaService;

    @MockitoBean
    private PersonaService personaService;

    // ─── GET ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /facturasEditar devuelve 200 y la vista correcta con formBean vacío")
    void getAltaDevuelveVista() throws Exception {
        when(personaService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/facturasEditar"))
                .andExpect(status().isOk())
                .andExpect(view().name("facturasEditar"))
                .andExpect(model().attributeExists("formBean"));
    }

    // ─── POST alta exitosa ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST alta válida: redirige a facturasBuscar y llama al servicio")
    void postAltaValidaRedirige() throws Exception {
        when(personaService.getAll()).thenReturn(Collections.emptyList());
        doNothing().when(facturaService).save(any(Factura.class), isNull());

        mockMvc.perform(post("/facturasEditar")
                .param("action", "Aceptar")
                .param("concepto", "Alquiler enero")
                .param("fechaEmision", "2026-01-01")
                .param("fechaVencimiento", "2026-01-31")
                .param("importe", "50000.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/facturasBuscar"));

        verify(facturaService).save(any(Factura.class), isNull());
    }

    @Test
    @DisplayName("POST alta: el estado NO se envía desde el form en nueva factura (no debe causar 500)")
    void postAltaSinEstadoNoCausa500() throws Exception {
        // Este es exactamente el bug: si el form enviaba estado="" Spring explotaba.
        // Ahora el campo no se envía para facturas nuevas y el test debe pasar sin errores.
        when(personaService.getAll()).thenReturn(Collections.emptyList());
        doNothing().when(facturaService).save(any(Factura.class), isNull());

        mockMvc.perform(post("/facturasEditar")
                .param("action", "Aceptar")
                .param("concepto", "Alquiler febrero")
                .param("fechaEmision", "2026-02-01")
                .param("fechaVencimiento", "2026-02-28")
                .param("importe", "55000.00"))
                // Sin param("estado", ...) — simula exactamente el form de nueva factura
                .andExpect(status().is3xxRedirection());
    }

    // ─── POST validación fallida ──────────────────────────────────────────────

    @Test
    @DisplayName("POST sin concepto: vuelve a la vista con error de validación")
    void postSinConceptoVuelveConError() throws Exception {
        when(personaService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/facturasEditar")
                .param("action", "Aceptar")
                // concepto ausente
                .param("fechaEmision", "2026-01-01")
                .param("fechaVencimiento", "2026-01-31")
                .param("importe", "50000.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("facturasEditar"))
                .andExpect(model().hasErrors());

        verify(facturaService, never()).save(any(), any());
    }

    @Test
    @DisplayName("POST sin importe: vuelve a la vista con error de validación")
    void postSinImporteVuelveConError() throws Exception {
        when(personaService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/facturasEditar")
                .param("action", "Aceptar")
                .param("concepto", "Alquiler marzo")
                .param("fechaEmision", "2026-03-01")
                .param("fechaVencimiento", "2026-03-31"))
                // importe ausente
                .andExpect(status().isOk())
                .andExpect(view().name("facturasEditar"))
                .andExpect(model().hasErrors());
    }

    // ─── POST error de servicio ───────────────────────────────────────────────

    @Test
    @DisplayName("POST cuando el servicio lanza Excepcion: vuelve a la vista con el error")
    void postErrorServicioVuelveConMensaje() throws Exception {
        when(personaService.getAll()).thenReturn(Collections.emptyList());
        Excepcion ex = new Excepcion("Factura inválida por regla de negocio", null);
        org.mockito.Mockito.doThrow(ex).when(facturaService).save(any(), any());

        mockMvc.perform(post("/facturasEditar")
                .param("action", "Aceptar")
                .param("concepto", "Alquiler abril")
                .param("fechaEmision", "2026-04-01")
                .param("fechaVencimiento", "2026-04-30")
                .param("importe", "60000.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("facturasEditar"));
    }

    // ─── POST Cancelar ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST Cancelar: redirige a facturasBuscar sin llamar al servicio")
    void postCancelarRedirige() throws Exception {
        when(personaService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/facturasEditar")
                .param("action", "Cancelar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/facturasBuscar"));

        verify(facturaService, never()).save(any(), any());
    }
}
