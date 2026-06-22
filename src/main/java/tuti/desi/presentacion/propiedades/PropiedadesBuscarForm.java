package tuti.desi.presentacion.propiedades;

import tuti.desi.entidades.EstadoPropiedad;
import tuti.desi.entidades.TipoPropiedad;

public class PropiedadesBuscarForm {

    private String direccion;
    private Long ciudadSeleccionada;
    private TipoPropiedad tipo;
    private EstadoPropiedad estado;

    public String getDireccion() {
        return normalizar(direccion);
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Long getCiudadSeleccionada() {
        return ciudadSeleccionada;
    }

    public void setCiudadSeleccionada(Long ciudadSeleccionada) {
        this.ciudadSeleccionada = ciudadSeleccionada;
    }

    public TipoPropiedad getTipo() {
        return tipo;
    }

    public void setTipo(TipoPropiedad tipo) {
        this.tipo = tipo;
    }

    public EstadoPropiedad getEstado() {
        return estado;
    }

    public void setEstado(EstadoPropiedad estado) {
        this.estado = estado;
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
