package tuti.desi.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estado_publicadon") // Nombre exacto de tu tabla en la BD
public class HistorialEstadoPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Le dice a Hibernate que en la BD guarde el texto ('ACTIVA', 'PAUSADA')
    @Column(name = "estado_anterior")
    private EstadoPublicacion estadoAnterior; 

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo")
    private EstadoPublicacion estadoNuevo; 

    @Column(name = "fecha_hora")
    private LocalDateTime fechaCambio;

    @ManyToOne
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    // --- Constructores ---
    public HistorialEstadoPublicacion() {}

    public HistorialEstadoPublicacion(EstadoPublicacion estadoAnterior, EstadoPublicacion estadoNuevo, LocalDateTime fechaCambio, Publicacion publicacion) {
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = fechaCambio;
        this.publicacion = publicacion;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoPublicacion getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(EstadoPublicacion estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public EstadoPublicacion getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(EstadoPublicacion estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

    public Publicacion getPublicacion() { return publicacion; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }
}