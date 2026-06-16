package tuti.desi.presentacion.contratos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tuti.desi.entidades.Contrato;

public class ContratoForm {
	
	private Long id;

	@NotNull(message = "La propiedad es obligatoria")
	private Long idPropiedad;
	
	@NotNull(message = "El propietario es obligatorio")
	private Long idPropietario; //NO estaba en la historia de usuario pero la agrego porque estaba en la BD

	@NotNull(message = "El inquilino es obligatorio")
	private Long idInquilino; //El inquilino deberá seleccionarse desde una lista de personas registradas y no eliminadas.
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio; //La fecha de inicio deberá ser una fecha válida

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor a 1 mes")
    private Integer duracionMeses; //La duración en meses debe ser un número positivo

    @NotNull(message = "El importe mensual es obligatorio")
    private BigDecimal importeMensual; //El importe mensual deberá ser un número positivo.

    @NotNull(message = "El día de vencimiento es obligatorio")
    @Pattern(regexp = "\\d{1,31}", message = "El día debe ser entre 1 y 31")
    private Integer diaVencimientoMensual; //El día de vencimiento mensual deberá ser un número válido entre 1 y 31.

    @Size(max = 500) 
    private String descripcion;

    public ContratoForm() {
    }

    public ContratoForm(Contrato contrato) {
        this.id = contrato.getId();
    
        this.idPropiedad =
                contrato.getPropiedad() == null
                ? null
                : contrato.getPropiedad().getId();
        
        this.idPropietario =
                contrato.getPropietario() == null
                ? null
                : contrato.getPropietario().getId();

        this.idInquilino =
                contrato.getInquilino() == null
                ? null
                : contrato.getInquilino().getId();
        
        this.fechaInicio = contrato.getFechaInicio();
        this.duracionMeses = contrato.getDuracionMeses();
        this.importeMensual = contrato.getImporteMensual();
        this.diaVencimientoMensual = contrato.getDiaVencimientoMensual();
        this.descripcion = contrato.getDescripcion();
    }

    public Contrato toPojo() {
    	
        Contrato contrato = new Contrato();
        contrato.setId(id);

        contrato.setFechaInicio(fechaInicio);
        contrato.setDuracionMeses(duracionMeses);
        contrato.setImporteMensual(importeMensual);
        contrato.setDiaVencimientoMensual(diaVencimientoMensual);
        contrato.setDescripcion(descripcion);

        return contrato;
    }

	
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdPropiedad() {
		return idPropiedad;
	}

	public void setIdPropiedad(Long idPropiedad) {
		this.idPropiedad = idPropiedad;
	}

	public Long getIdInquilino() {
		return idInquilino;
	}

	public void setIdInquilino(Long idInquilino) {
		this.idInquilino = idInquilino;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Integer getDuracionMeses() {
		return duracionMeses;
	}

	public void setDuracionMeses(Integer duracionMeses) {
		this.duracionMeses = duracionMeses;
	}

	public BigDecimal getImporteMensual() {
		return importeMensual;
	}

	public void setImporteMensual(BigDecimal importeMensual) {
		this.importeMensual = importeMensual;
	}

	public Integer getDiaVencimientoMensual() {
		return diaVencimientoMensual;
	}

	public void setDiaVencimientoMensual(Integer diaVencimientoMensual) {
		this.diaVencimientoMensual = diaVencimientoMensual;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getIdPropietario() {
		return idPropietario;
	}

	public void setIdPropietario(Long idPropietario) {
		this.idPropietario = idPropietario;
	}

   }



/*
● Deberá guardarse registro de fechas de cada cambio de estado del
contrato en un historial de estados (no es necesario que el mismo sea
visible, pero sí que esté registrado en la base de datos. Idealmente
también dejaríamos registro del usuario que hizo dicho cambio, pero
obviaremos esto en el TP por simplicidad)
*/