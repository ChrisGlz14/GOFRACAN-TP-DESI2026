package tuti.desi.presentacion.propiedades;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tuti.desi.entidades.EstadoPropiedad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.TipoPropiedad;

// igual que CiudadForm, en vez del objeto Ciudad/Persona guardo solo los ids, es mas facil para el form
public class PropiedadForm {

    private Long id;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La ciudad es obligatoria")
    private Long idCiudad;

    @NotNull(message = "El tipo de propiedad es obligatorio")
    private TipoPropiedad tipo;

    @NotNull(message = "La cantidad de ambientes es obligatoria")
    @Positive(message = "La cantidad de ambientes debe ser un número entero positivo")
    private Integer cantidadAmbientes;

    @NotNull(message = "Los metros cuadrados son obligatorios")
    @Positive(message = "Los metros cuadrados deben ser un número positivo")
    private BigDecimal metrosCuadrados;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    // el estado no lo pongo obligatorio, si no eligen nada queda DISPONIBLE
    private EstadoPropiedad estado;

    @NotNull(message = "El propietario es obligatorio")
    private Long idPropietario;

    public PropiedadForm() {
        // al dar de alta arranca en DISPONIBLE
        this.estado = EstadoPropiedad.DISPONIBLE;
    }

    public PropiedadForm(Propiedad p) {
        this.id = p.getId();
        this.direccion = p.getDireccion();
        this.idCiudad = p.getCiudad() != null ? p.getCiudad().getId() : null;
        this.tipo = p.getTipo();
        this.cantidadAmbientes = p.getCantidadAmbientes();
        this.metrosCuadrados = p.getMetrosCuadrados();
        this.descripcion = p.getDescripcion();
        this.estado = p.getEstado();
        this.idPropietario = p.getPropietario() != null ? p.getPropietario().getId() : null;
    }

    // la ciudad y el propietario los carga el controller con los ids, como hace CiudadForm con la provincia
    public Propiedad toPojo() {
        Propiedad p = new Propiedad();
        p.setId(this.id);
        p.setDireccion(this.direccion);
        p.setTipo(this.tipo);
        p.setCantidadAmbientes(this.cantidadAmbientes);
        p.setMetrosCuadrados(this.metrosCuadrados);
        p.setDescripcion(this.descripcion);
        // si eligieron un estado lo uso, sino queda el DISPONIBLE que ya trae la propiedad nueva
        if (this.estado != null) {
            p.setEstado(this.estado);
        }
        return p;
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

    public Long getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Long idCiudad) {
        this.idCiudad = idCiudad;
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

    public Long getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(Long idPropietario) {
        this.idPropietario = idPropietario;
    }
}
