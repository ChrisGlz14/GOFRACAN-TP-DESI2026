package tuti.desi.servicios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tuti.desi.accesoDatos.IFacturaRepo;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.HistorialEstadoFactura;
import tuti.desi.entidades.MedioPago;
import tuti.desi.entidades.Persona;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;

@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private IFacturaRepo facturaRepo;

    @Autowired
    private HistorialEstadoFacturaService historialService;

    @Autowired
    private PersonaService personaService;

    // ─────────────────────────────────────────────────────────────────────────
    // Consultas
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public List<Factura> getAll() {
        return facturaRepo.findAllActivas();
    }

    @Override
    public List<Factura> filtrar(EstadoFactura estado, String concepto,
                                 LocalDate fechaDesde, LocalDate fechaHasta, Long idPersona) {
        String conceptoFiltro = (concepto == null || concepto.isBlank()) ? null : concepto.trim();
        return facturaRepo.filtrar(estado, conceptoFiltro, fechaDesde, fechaHasta, idPersona);
    }

    @Override
    public Factura getFacturaById(Long idFactura) {
        Factura factura = facturaRepo.findById(idFactura)
                .orElseThrow(() -> new EntidadNoEncontradaException("la factura", idFactura));
        if (Boolean.TRUE.equals(factura.getEliminada())) {
            throw new EntidadNoEncontradaException("la factura", idFactura);
        }
        return factura;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Alta / Modificación
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void save(Factura factura, Long idPersona) throws Excepcion {

        boolean esAlta = (factura.getId() == null);

        if (esAlta) {
            // Estado inicial siempre PENDIENTE
            factura.setEstado(EstadoFactura.PENDIENTE);
            // Limpiar datos de pago que no aplican al crear
            limpiarDatosPago(factura);

        } else {
            // Modificación: recuperar la entidad persistida para validaciones
            Factura existente = getFacturaById(factura.getId());

            if (existente.getEstado() == EstadoFactura.PAGADA) {
                throw new Excepcion("No se puede modificar una factura ya PAGADA.", null);
            }
            if (existente.getEstado() == EstadoFactura.ANULADA) {
                throw new Excepcion("No se puede modificar una factura ANULADA.", null);
            }

            // Preservar estado y datos de pago que se gestionan por métodos separados
            factura.setEstado(existente.getEstado());
            factura.setFechaPago(existente.getFechaPago());
            factura.setMedioPago(existente.getMedioPago());
            factura.setImportePagado(existente.getImportePagado());
            factura.setInteres(existente.getInteres());
            factura.setEliminada(existente.getEliminada());
        }

        // Asociar persona (inquilino) si viene informada
        if (idPersona != null) {
            Persona persona = personaService.getPersonaById(idPersona);
            factura.setPersona(persona);
        } else {
            factura.setPersona(null);
        }

        Factura guardada = facturaRepo.save(factura);

        // En alta: registrar el primer estado en el historial
        if (esAlta) {
            historialService.save(
                    new HistorialEstadoFactura(guardada, null, EstadoFactura.PENDIENTE));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Baja lógica
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void bajaLogica(Long id) throws Excepcion {
        Factura factura = getFacturaById(id);

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new Excepcion("No se puede eliminar una factura PAGADA.", null);
        }

        factura.setEliminada(true);
        facturaRepo.save(factura);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cambio de estado
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void cambiarEstado(Long idFactura, EstadoFactura nuevoEstado) throws Excepcion {
        Factura factura = getFacturaById(idFactura);

        validarTransicionEstado(factura.getEstado(), nuevoEstado);

        // Si deja de estar PAGADA, limpiar datos de pago
        if (nuevoEstado != EstadoFactura.PAGADA) {
            limpiarDatosPago(factura);
        }

        EstadoFactura anterior = factura.getEstado();
        factura.setEstado(nuevoEstado);
        facturaRepo.save(factura);

        historialService.save(new HistorialEstadoFactura(factura, anterior, nuevoEstado));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Registro de pago
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void registrarPago(Long idFactura, LocalDate fechaPago,
                              MedioPago medioPago, BigDecimal importePagado) throws Excepcion {

        if (fechaPago == null) {
            throw new Excepcion("La fecha de pago es obligatoria para registrar el pago.", "fechaPago");
        }
        if (medioPago == null) {
            throw new Excepcion("El medio de pago es obligatorio.", "medioPago");
        }
        if (importePagado == null || importePagado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Excepcion("El importe pagado debe ser mayor a cero.", "importePagado");
        }

        Factura factura = getFacturaById(idFactura);

        if (factura.getEstado() != EstadoFactura.PENDIENTE
                && factura.getEstado() != EstadoFactura.VENCIDA) {
            throw new Excepcion(
                    "Solo se pueden pagar facturas en estado PENDIENTE o VENCIDA.", "estado");
        }

        factura.setFechaPago(fechaPago);
        factura.setMedioPago(medioPago);
        factura.setImportePagado(importePagado);

        // Calcular interés simple para facturas vencidas (0 % por defecto — el cálculo real queda pendiente)
        if (factura.getEstado() == EstadoFactura.VENCIDA) {
            factura.setInteres(BigDecimal.ZERO);
        }

        EstadoFactura anterior = factura.getEstado();
        factura.setEstado(EstadoFactura.PAGADA);
        facturaRepo.save(factura);

        historialService.save(new HistorialEstadoFactura(factura, anterior, EstadoFactura.PAGADA));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    private void limpiarDatosPago(Factura factura) {
        factura.setFechaPago(null);
        factura.setMedioPago(null);
        factura.setImportePagado(null);
        factura.setInteres(null);
    }

    /**
     * Valida que la transición entre estados sea permitida.
     * Reglas: PAGADA y ANULADA son estados finales.
     */
    private void validarTransicionEstado(EstadoFactura actual, EstadoFactura nuevo) throws Excepcion {
        if (actual == EstadoFactura.PAGADA) {
            throw new Excepcion("Una factura PAGADA no puede cambiar de estado.", "estado");
        }
        if (actual == EstadoFactura.ANULADA) {
            throw new Excepcion("Una factura ANULADA no puede cambiar de estado.", "estado");
        }
        if (nuevo == EstadoFactura.PAGADA) {
            throw new Excepcion(
                    "Para registrar un pago usá la acción 'Registrar Pago'.", "estado");
        }
    }
}
