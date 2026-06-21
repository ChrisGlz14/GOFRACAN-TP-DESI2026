package tuti.desi.servicios;

import java.util.List;

import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.propiedades.PropiedadesBuscarForm;

// La interfaz define QUÉ se puede hacer (el contrato), no el CÓMO.
// El controller depende de esta interfaz.
public interface PropiedadService {

    List<Propiedad> obtenerTodas();

    List<Propiedad> filter(PropiedadesBuscarForm filter);

    Propiedad guardar(Propiedad propiedad) throws Excepcion;

    Propiedad buscarPorId(Long id);

    void eliminar(Long id) throws Excepcion;



}
