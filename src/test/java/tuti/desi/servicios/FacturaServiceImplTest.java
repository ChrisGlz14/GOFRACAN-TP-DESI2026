package tuti.desi.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tuti.desi.accesoDatos.IFacturaRepo;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.HistorialEstadoFactura;
import tuti.desi.entidades.MedioPago;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;

/**
 * Tests unitarios para FacturaServiceImpl.
 * No levanta Spring ni base de datos — todo se mockea con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class FacturaServiceImplTest {

    @Mock
    private IFacturaRepo facturaRepo;

    @Mock
    private HistorialEstadoFacturaService historialService;

    @Mock
    private PersonaService personaService;

    @InjectMocks
    private FacturaServiceImpl service;

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Factura facturaBase() {
        Factura f = new Factura();
        f.setConcepto("Alquiler mes 1");
        f.setFechaEmision(LocalDate.of(2026, 1, 1));
        f.setFechaVencimiento(LocalDate.of(2026, 1, 31));
        f.setImporte(new BigDecimal("50000.00"));
        f.setEliminada(false);
        return f;
    }

    private Factura facturaGuardada(Long id, EstadoFactura estado) {
        Factura f = facturaBase();
        f.setId(id);
        f.setEstado(estado);
        return f;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Alta (save)
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("save() — alta de factura")
    class SaveAlta {

        @Test
        @DisplayName("El estado inicial siempre debe ser PENDIENTE, sin importar qué venga del form")
        void altaSiempreSetaPendiente() throws Excepcion {
            Factura nueva = facturaBase();
            // Simulamos que el form mandó PAGADA (no debería pasar, pero lo probamos igual)
            nueva.setEstado(EstadoFactura.PAGADA);

            Factura savedMock = facturaBase();
            savedMock.setId(1L);
            savedMock.setEstado(EstadoFactura.PENDIENTE);
            when(facturaRepo.save(any(Factura.class))).thenReturn(savedMock);

            service.save(nueva, null);

            // Capturamos lo que le pasamos al repo para verificar el estado
            ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
            verify(facturaRepo).save(captor.capture());
            assertThat(captor.getValue().getEstado())
                    .as("El servicio debe forzar estado PENDIENTE en el alta")
                    .isEqualTo(EstadoFactura.PENDIENTE);
        }

        @Test
        @DisplayName("En alta debe guardarse un registro en el historial con estadoNuevo=PENDIENTE")
        void altaGuardaHistorialConPendiente() throws Excepcion {
            Factura nueva = facturaBase();

            Factura savedMock = facturaBase();
            savedMock.setId(5L);
            savedMock.setEstado(EstadoFactura.PENDIENTE);
            when(facturaRepo.save(any(Factura.class))).thenReturn(savedMock);

            service.save(nueva, null);

            // El historialService debe recibir exactamente un registro
            ArgumentCaptor<HistorialEstadoFactura> captor =
                    ArgumentCaptor.forClass(HistorialEstadoFactura.class);
            verify(historialService, times(1)).save(captor.capture());

            HistorialEstadoFactura historial = captor.getValue();
            // La tabla solo guarda el estado nuevo (el resultante del cambio)
            assertThat(historial.getEstadoNuevo())
                    .as("estadoNuevo debe ser PENDIENTE")
                    .isEqualTo(EstadoFactura.PENDIENTE);
        }

        @Test
        @DisplayName("En alta los datos de pago deben quedar en null aunque vengan del form")
        void altaLimpiaDatosDePago() throws Excepcion {
            Factura nueva = facturaBase();
            nueva.setFechaPago(LocalDate.now());
            nueva.setMedioPago(MedioPago.EFECTIVO);
            nueva.setImportePagado(new BigDecimal("50000"));

            Factura savedMock = facturaBase();
            savedMock.setId(2L);
            savedMock.setEstado(EstadoFactura.PENDIENTE);
            when(facturaRepo.save(any(Factura.class))).thenReturn(savedMock);

            service.save(nueva, null);

            ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
            verify(facturaRepo).save(captor.capture());
            Factura guardada = captor.getValue();

            assertThat(guardada.getFechaPago()).isNull();
            assertThat(guardada.getMedioPago()).isNull();
            assertThat(guardada.getImportePagado()).isNull();
            assertThat(guardada.getInteres()).isNull();
        }

        @Test
        @DisplayName("En alta con idPersona válido debe asociarse la persona")
        void altaAsociaPersonaSiHayId() throws Excepcion {
            Factura nueva = facturaBase();

            tuti.desi.entidades.Persona persona = new tuti.desi.entidades.Persona();
            persona.setId(10L);
            when(personaService.getPersonaById(10L)).thenReturn(persona);

            Factura savedMock = facturaBase();
            savedMock.setId(3L);
            savedMock.setEstado(EstadoFactura.PENDIENTE);
            savedMock.setPersona(persona);
            when(facturaRepo.save(any(Factura.class))).thenReturn(savedMock);

            service.save(nueva, 10L);

            ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
            verify(facturaRepo).save(captor.capture());
            assertThat(captor.getValue().getPersona()).isNotNull();
            assertThat(captor.getValue().getPersona().getId()).isEqualTo(10L);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Edición (save con id)
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("save() — edición de factura")
    class SaveEdicion {

        @Test
        @DisplayName("No se puede modificar una factura PAGADA")
        void noModificaPagada() {
            Factura existente = facturaGuardada(1L, EstadoFactura.PAGADA);
            when(facturaRepo.findById(1L)).thenReturn(Optional.of(existente));

            Factura form = facturaBase();
            form.setId(1L);

            assertThatThrownBy(() -> service.save(form, null))
                    .isInstanceOf(Excepcion.class)
                    .hasMessageContaining("PAGADA");

            verify(facturaRepo, never()).save(any());
        }

        @Test
        @DisplayName("No se puede modificar una factura ANULADA")
        void noModificaAnulada() {
            Factura existente = facturaGuardada(2L, EstadoFactura.ANULADA);
            when(facturaRepo.findById(2L)).thenReturn(Optional.of(existente));

            Factura form = facturaBase();
            form.setId(2L);

            assertThatThrownBy(() -> service.save(form, null))
                    .isInstanceOf(Excepcion.class)
                    .hasMessageContaining("ANULADA");
        }

        @Test
        @DisplayName("Edición exitosa de factura PENDIENTE preserva el estado original")
        void edicionPreservaEstado() throws Excepcion {
            Factura existente = facturaGuardada(3L, EstadoFactura.PENDIENTE);
            when(facturaRepo.findById(3L)).thenReturn(Optional.of(existente));
            when(facturaRepo.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

            Factura form = facturaBase();
            form.setId(3L);
            form.setConcepto("Alquiler mes 2 (editado)");

            service.save(form, null);

            ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
            verify(facturaRepo).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
            assertThat(captor.getValue().getConcepto()).isEqualTo("Alquiler mes 2 (editado)");
            // En edición NO se debe guardar historial
            verify(historialService, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Baja lógica
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("bajaLogica()")
    class BajaLogica {

        @Test
        @DisplayName("No se puede eliminar una factura PAGADA")
        void noBajaPagada() {
            Factura pagada = facturaGuardada(1L, EstadoFactura.PAGADA);
            when(facturaRepo.findById(1L)).thenReturn(Optional.of(pagada));

            assertThatThrownBy(() -> service.bajaLogica(1L))
                    .isInstanceOf(Excepcion.class)
                    .hasMessageContaining("PAGADA");
        }

        @Test
        @DisplayName("Baja lógica de factura PENDIENTE debe marcar eliminada=true")
        void bajaMarcaEliminada() throws Excepcion {
            Factura pendiente = facturaGuardada(2L, EstadoFactura.PENDIENTE);
            when(facturaRepo.findById(2L)).thenReturn(Optional.of(pendiente));
            when(facturaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.bajaLogica(2L);

            verify(facturaRepo).save(argThat(f -> Boolean.TRUE.equals(f.getEliminada())));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Registrar pago
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("registrarPago()")
    class RegistrarPago {

        @Test
        @DisplayName("Pago exitoso cambia estado a PAGADA y guarda historial")
        void pagoExitoso() throws Excepcion {
            Factura pendiente = facturaGuardada(1L, EstadoFactura.PENDIENTE);
            when(facturaRepo.findById(1L)).thenReturn(Optional.of(pendiente));
            when(facturaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.registrarPago(1L, LocalDate.now(), MedioPago.TRANSFERENCIA,
                    new BigDecimal("50000.00"));

            ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
            verify(facturaRepo).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo(EstadoFactura.PAGADA);
            assertThat(captor.getValue().getMedioPago()).isEqualTo(MedioPago.TRANSFERENCIA);

            ArgumentCaptor<HistorialEstadoFactura> histCaptor =
                    ArgumentCaptor.forClass(HistorialEstadoFactura.class);
            verify(historialService).save(histCaptor.capture());
            // La tabla solo guarda el estado nuevo (resultante del cambio), no el anterior
            assertThat(histCaptor.getValue().getEstadoNuevo()).isEqualTo(EstadoFactura.PAGADA);
        }

        @Test
        @DisplayName("No se puede pagar una factura ANULADA")
        void noPagaAnulada() {
            Factura anulada = facturaGuardada(2L, EstadoFactura.ANULADA);
            when(facturaRepo.findById(2L)).thenReturn(Optional.of(anulada));

            assertThatThrownBy(() -> service.registrarPago(2L, LocalDate.now(),
                    MedioPago.EFECTIVO, new BigDecimal("100")))
                    .isInstanceOf(Excepcion.class);
        }

        @Test
        @DisplayName("Lanza excepción si falta fecha de pago")
        void fallaSinFechaPago() {
            assertThatThrownBy(() -> service.registrarPago(1L, null,
                    MedioPago.EFECTIVO, new BigDecimal("100")))
                    .isInstanceOf(Excepcion.class)
                    .hasMessageContaining("fecha");
        }

        @Test
        @DisplayName("Lanza excepción si falta medio de pago")
        void fallaSinMedioPago() {
            assertThatThrownBy(() -> service.registrarPago(1L, LocalDate.now(),
                    null, new BigDecimal("100")))
                    .isInstanceOf(Excepcion.class)
                    .hasMessageContaining("medio");
        }

        @Test
        @DisplayName("Lanza excepción si importe pagado es cero o negativo")
        void fallaImporteCero() {
            assertThatThrownBy(() -> service.registrarPago(1L, LocalDate.now(),
                    MedioPago.EFECTIVO, BigDecimal.ZERO))
                    .isInstanceOf(Excepcion.class)
                    .hasMessageContaining("importe");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getFacturaById
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getFacturaById()")
    class GetById {

        @Test
        @DisplayName("Lanza EntidadNoEncontradaException si la factura no existe")
        void noEncuentra() {
            when(facturaRepo.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.getFacturaById(99L))
                    .isInstanceOf(EntidadNoEncontradaException.class);
        }

        @Test
        @DisplayName("Lanza EntidadNoEncontradaException si la factura está eliminada")
        void noEncuentraEliminada() {
            Factura eliminada = facturaGuardada(1L, EstadoFactura.PENDIENTE);
            eliminada.setEliminada(true);
            when(facturaRepo.findById(1L)).thenReturn(Optional.of(eliminada));
            assertThatThrownBy(() -> service.getFacturaById(1L))
                    .isInstanceOf(EntidadNoEncontradaException.class);
        }
    }
}
