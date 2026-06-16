package tuti.desi.presentacion.facturas;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.MedioPago;

public class FacturaForm {

    private Long id;

    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 200, message = "El concepto no puede tener más de 200 caracteres")
    private String concepto;

    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a cero")
    private BigDecimal importe;

    // Solo lectura desde la vista — se gestiona por el servicio
    private EstadoFactura estado;

    // Inquilino asociado
    private Long idPersona;

    // ─── Datos de pago (solo visibles/editables cuando estado = PAGADA) ─────
    private LocalDate fechaPago;
    private MedioPago medioPago;
    private BigDecimal importePagado;
    private BigDecimal interes;

    public FacturaForm() {
    }

    public FacturaForm(Factura factura) {
        this.id              = factura.getId();
        this.concepto        = factura.getConcepto();
        this.fechaEmision    = factura.getFechaEmision();
        this.fechaVencimiento = factura.getFechaVencimiento();
        this.importe         = factura.getImporte();
        this.estado          = factura.getEstado();
        this.idPersona       = factura.getPersona() != null ? factura.getPersona().getId() : null;
        this.fechaPago       = factura.getFechaPago();
        this.medioPago       = factura.getMedioPago();
        this.importePagado   = factura.getImportePagado();
        this.interes         = factura.getInteres();
    }

    /** Convierte el form a entidad (sin resolver la Persona — eso lo hace el servicio). */
    public Factura toPojo() {
        Factura f = new Factura();
        f.setId(id);
        f.setConcepto(concepto);
        f.setFechaEmision(fechaEmision);
        f.setFechaVencimiento(fechaVencimiento);
        f.setImporte(importe);
        f.setEstado(estado);
        f.setFechaPago(fechaPago);
        f.setMedioPago(medioPago);
        f.setImportePagado(importePagado);
        f.setInteres(interes);
        return f;
    }

    // ─── Getters / Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public EstadoFactura getEstado() { return estado; }
    public void setEstado(EstadoFactura estado) { this.estado = estado; }

    public Long getIdPersona() { return idPersona; }
    public void setIdPersona(Long idPersona) { this.idPersona = idPersona; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public MedioPago getMedioPago() { return medioPago; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }

    public BigDecimal getImportePagado() { return importePagado; }
    public void setImportePagado(BigDecimal importePagado) { this.importePagado = importePagado; }

    public BigDecimal getInteres() { return interes; }
    public void setInteres(BigDecimal interes) { this.interes = interes; }
}
