package tuti.desi.servicios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.MedioPago;
import tuti.desi.excepciones.Excepcion;

public interface FacturaService {

    /** Todas las facturas no eliminadas. */
    List<Factura> getAll();

    /**
     * Filtra facturas. Cualquier parámetro puede ser null para ignorarlo.
     * fechaDesde/fechaHasta se comparan contra fechaVencimiento.
     */
    List<Factura> filtrar(EstadoFactura estado, String concepto,
                          LocalDate fechaDesde, LocalDate fechaHasta, Long idPersona);

    /**
     * Crea o actualiza una factura.
     * - Alta: estado forzado a PENDIENTE; idPersona opcional.
     * - Edición: no se puede modificar si está PAGADA o ANULADA.
     */
    void save(Factura factura, Long idPersona) throws Excepcion;

    Factura getFacturaById(Long idFactura);

    /** Baja lógica. No permitida si la factura está PAGADA. */
    void bajaLogica(Long id) throws Excepcion;

    /** Cambia estado y registra en historial. */
    void cambiarEstado(Long idFactura, EstadoFactura nuevoEstado) throws Excepcion;

    /**
     * Registra el pago: exige fechaPago, medioPago e importePagado,
     * cambia estado a PAGADA y guarda historial.
     */
    void registrarPago(Long idFactura, LocalDate fechaPago,
                       MedioPago medioPago, BigDecimal importePagado) throws Excepcion;
}
