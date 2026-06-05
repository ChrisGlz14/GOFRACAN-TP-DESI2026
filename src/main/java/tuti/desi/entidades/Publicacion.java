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


	@Entity
	@Table(name = "publicaciones")
	public class Publicacion {
	
		@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
	    @Column(nullable = false)
	    
	    private EstadoPublicacion estado; 
	    @Column(nullable = false)
	    
	    private boolean eliminada; 
	    @ManyToOne
	    @JoinColumn(name = "propiedad_id", nullable = false)
	    private Propiedad propiedad; 

	    public Publicacion() {
	        this.estado = EstadoPublicacion.ACTIVA; 
	        this.eliminada = false;}
	     // Getters y Setters
	        public Long getId() { return id; }
	        public void setId(Long id) { this.id = id; }
	        public BigDecimal getPrecioMensual() { return precioMensual; }
	        public void setPrecioMensual(BigDecimal precioMensual) { this.precioMensual = precioMensual; }
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
	        public void setPropiedad(Propiedad propiedad) { this.propiedad = propiedad; 
	    }
	    }
