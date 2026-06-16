package tuti.desi.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 200, message = "El concepto no puede tener más de 200 caracteres")
    @Column(nullable = false)
    private String concepto;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaEmision;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a cero")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal importe;

    // Sin @NotNull aquí: el servicio siempre fuerza PENDIENTE en alta,
    // y si pusiera @NotNull la validación de Bean Validation explotaría
    // antes de que el servicio pueda asignarlo.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;

    // Inquilino al que pertenece esta factura
    @ManyToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    // ─── Datos de pago (solo se completan cuando estado = PAGADA) ───────────

    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    private MedioPago medioPago;

    @Column(precision = 15, scale = 2)
    private BigDecimal importePagado;

    @Column(precision = 15, scale = 2)
    private BigDecimal interes;

    // Baja lógica — nunca borramos el registro para preservar el historial
    @Column(nullable = false)
    private Boolean eliminada = false;

    public Factura() {
    }
}
