package tuti.desi.entidades;

import jakarta.persistence.*;  
import java.math.BigDecimal;    
import java.time.LocalDate;

@Entity // la clase CONTRATO es una tabla en la BD
@Table(name = "contrato") 

public class Contrato {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "duracion_meses", nullable = false)
    private Integer duracionMeses;

    @Column(name = "importe_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeMensual;

    @Column(name = "dia_vencimiento_mensual", nullable = false)
    private Integer diaVencimientoMensual;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoContrato estado;

    @Column(name = "eliminado")
    private Boolean eliminado = false;

    //@ManyToOne(optional = false)
    //@JoinColumn(name = "propiedad_id", nullable = false)
    //private Propiedad propiedad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Persona propietario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "inquilino_id", nullable = false)
    private Persona inquilino;

    //constructor
    public Contrato() {}

    //getters y setters
    public Long getId() { return id; }
    
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public Integer getDuracionMeses() { return duracionMeses; }
    public void setDuracionMeses(Integer duracionMeses) { this.duracionMeses = duracionMeses; }

    public BigDecimal getImporteMensual() { return importeMensual; }
    public void setImporteMensual(BigDecimal importeMensual) { this.importeMensual = importeMensual; }

    public Integer getDiaVencimientoMensual() { return diaVencimientoMensual; }
    public void setDiaVencimientoMensual(Integer diaVencimientoMensual) { this.diaVencimientoMensual = diaVencimientoMensual; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoContrato getEstado() { return estado; }
    public void setEstado(EstadoContrato estado) { this.estado = estado; }

    public Boolean getEliminado() { return eliminado; }
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }

    //public Propiedad getPropiedad() { return propiedad; }
    //public void setPropiedad(Propiedad propiedad) { this.propiedad = propiedad; }

    public Persona getPropietario() { return propietario; }
    public void setPropietario(Persona propietario) { this.propietario = propietario; }

    public Persona getInquilino() { return inquilino; }
    public void setInquilino(Persona inquilino) { this.inquilino = inquilino;
    }
 }
