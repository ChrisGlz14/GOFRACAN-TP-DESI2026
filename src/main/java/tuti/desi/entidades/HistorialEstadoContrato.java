package tuti.desi.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estado_contrato")
public class HistorialEstadoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoContrato estado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    public HistorialEstadoContrato() {}

    public HistorialEstadoContrato(Contrato contrato, EstadoContrato estado) {
        this.contrato = contrato;
        this.estado = estado;
        this.fechaHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoContrato getEstado() { return estado; }
    public void setEstado(EstadoContrato estado) { this.estado = estado; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Contrato getContrato() { return contrato; }
    public void setContrato(Contrato contrato) { this.contrato = contrato; }
}
