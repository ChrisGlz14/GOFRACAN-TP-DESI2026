package tuti.desi.entidades;

import java.math.BigDecimal;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "propiedad")
public class Propiedad {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "La dirección es obligatoria")
	@Column(nullable = false)
	private String direccion;

	@NotNull(message = "La ciudad es obligatoria")
	@ManyToOne(optional = false)
	@JoinColumn(name = "ciudad_id", nullable = false)
	private Ciudad ciudad;

	@NotNull(message = "El tipo de propiedad es obligatorio")
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo", nullable = false)
	private TipoPropiedad tipo;

	@NotNull(message = "La cantidad de ambientes es obligatoria")
	@Positive(message = "La cantidad de ambientes debe ser un número entero positivo")
	@Column(name = "cantidad_ambientes", nullable = false)
	private Integer cantidadAmbientes;

	@NotNull(message = "Los metros cuadrados son obligatorios")
	@Positive(message = "Los metros cuadrados deben ser un número positivo")
	@Column(name = "metros_cuadrados", nullable = false)
	private BigDecimal metrosCuadrados;

	@NotBlank(message = "La descripción es obligatoria")
	@Column(columnDefinition = "TEXT", nullable = false)
	private String descripcion;

	@NotNull(message = "El estado de disponibilidad es obligatorio")
	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	private EstadoPropiedad estado;

	@NotNull(message = "El propietario es obligatorio")
	@ManyToOne(optional = false)
	@JoinColumn(name = "propietario_id", nullable = false)
	private Persona propietario;

	// baja logica: al eliminar la marco en true y no la borro fisico (igual que Publicacion)
	private boolean eliminada;

	public Propiedad() {
		// Al dar de alta, el estado por defecto es DISPONIBLE
		this.estado = EstadoPropiedad.DISPONIBLE;
		this.eliminada = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Ciudad getCiudad() {
		return ciudad;
	}

	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}

	public TipoPropiedad getTipo() {
		return tipo;
	}

	public void setTipo(TipoPropiedad tipo) {
		this.tipo = tipo;
	}

	public Integer getCantidadAmbientes() {
		return cantidadAmbientes;
	}

	public void setCantidadAmbientes(Integer cantidadAmbientes) {
		this.cantidadAmbientes = cantidadAmbientes;
	}

	public BigDecimal getMetrosCuadrados() {
		return metrosCuadrados;
	}

	public void setMetrosCuadrados(BigDecimal metrosCuadrados) {
		this.metrosCuadrados = metrosCuadrados;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public EstadoPropiedad getEstado() {
		return estado;
	}

	public void setEstado(EstadoPropiedad estado) {
		this.estado = estado;
	}

	public Persona getPropietario() {
		return propietario;
	}

	public void setPropietario(Persona propietario) {
		this.propietario = propietario;
	}

	public boolean isEliminada() {
		return eliminada;
	}

	public void setEliminada(boolean eliminada) {
		this.eliminada = eliminada;
	}
}
