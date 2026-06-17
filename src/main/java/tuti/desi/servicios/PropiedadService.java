package tuti.desi.servicios;

import java.util.List;

import tuti.desi.entidades.Propiedad;

// La interfaz define QUÉ se puede hacer (el contrato), no el CÓMO.
// El controller depende de esta interfaz, no de la implementación concreta.
public interface PropiedadService {

    List<Propiedad> obtenerTodas();

    Propiedad guardar(Propiedad propiedad);

    Propiedad buscarPorId(Long id);
}
