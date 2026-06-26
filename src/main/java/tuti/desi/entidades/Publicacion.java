package tuti.desi.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

@Entity
@Table(name = "publicacion")
public class Publicacion {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "precio_mensual", nullable = false)
    private BigDecimal precioMensual;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String condiciones; 
    
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion; 

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPublicacion estado;
    
    private boolean eliminada; 
    
    @ManyToOne
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad; 
    @jakarta.persistence.OneToMany(mappedBy = "publicacion", cascade = jakarta.persistence.CascadeType.ALL)
    private java.util.List<HistorialEstadoPublicacion> historialEstados = new java.util.ArrayList<>();

    // Getter que pide el Servicio
    public java.util.List<HistorialEstadoPublicacion> getHistorialEstados() { 
        return historialEstados; 
    }
    // Constructor
    public Publicacion() {
        this.estado = EstadoPublicacion.ACTIVA; 
        this.eliminada = false;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getPrecioMensual() { return precioMensual; }
    
    // Sintaxis limpia para la asignación del BigDecimal
    public void setPrecioMensual(BigDecimal precioMensual) { 
        this.precioMensual = precioMensual; 
    }
    
    public String getCondiciones() { return condiciones; }
    public void setCondiciones(String condiciones) { this.condiciones = condiciones; }
    
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public EstadoPublicacion getEstado() { return estado; }
    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }
    
    public boolean isEliminada() { return eliminada; }
    public void setEliminada(boolean eliminada) { this.eliminada = eliminada; }
    
    public Propiedad getPropiedad() { return propiedad; }
    public void setPropiedad(Propiedad propiedad) { this.propiedad = propiedad; }
    public void setHistorialEstados(List<HistorialEstadoPublicacion> historialEstados) {
        this.historialEstados = historialEstados;
    }
}