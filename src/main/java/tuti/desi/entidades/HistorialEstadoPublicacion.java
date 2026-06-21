package tuti.desi.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estado_publicacion")
public class HistorialEstadoPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false) // Coincide con tu columna original 'estado'
    private EstadoPublicacion estado; 

    @Column(name = "fecha_hora", nullable = false) // Coincide con 'fecha_hora'
    private LocalDateTime fechaHora;

    
    @ManyToOne
    @JoinColumn(name = "publicacion_id", nullable = false) // Solo esto, sin el @JoinTable
    private Publicacion publicacion;
   
    // --- Constructores ---
    public HistorialEstadoPublicacion() {}

    public HistorialEstadoPublicacion(EstadoPublicacion estado, LocalDateTime fechaHora, Publicacion publicacion) {
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.publicacion = publicacion;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoPublicacion getEstado() { return estado; }
    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Publicacion getPublicacion() { return publicacion; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }
}