package tuti.desi.presentacion.facturas;

import java.time.LocalDate;

import tuti.desi.entidades.EstadoFactura;

public class FacturasBuscarForm {

    private EstadoFactura estado;
    private String concepto;

    /** Rango de fechaVencimiento */
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    /** Filtro por inquilino */
    private Long idPersona;

    public FacturasBuscarForm() {
    }

    public EstadoFactura getEstado() { return estado; }
    public void setEstado(EstadoFactura estado) { this.estado = estado; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public Long getIdPersona() { return idPersona; }
    public void setIdPersona(Long idPersona) { this.idPersona = idPersona; }
}
