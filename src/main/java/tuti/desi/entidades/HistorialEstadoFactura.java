package tuti.desi.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "historial_estado_factura")
public class HistorialEstadoFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Factura — columna física: factura_id
    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    // La tabla tiene UNA sola columna "estado" que guarda el estado nuevo al momento del cambio
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFactura estadoNuevo;

    // Fecha y hora del cambio — columna física: fecha_hora
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHoraCambio;

    // Constructor vacío requerido por Hibernate
    public HistorialEstadoFactura() {
    }

    // Constructor usado por el servicio al registrar cada cambio de estado
    public HistorialEstadoFactura(Factura factura, EstadoFactura estadoAnterior, EstadoFactura estadoNuevo) {
        this.factura = factura;
        // La tabla no almacena el estado anterior — solo el estado resultante del cambio
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraCambio = LocalDateTime.now();
    }
}
