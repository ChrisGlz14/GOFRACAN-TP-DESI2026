package tuti.desi.servicios;

import java.util.List;
import tuti.desi.entidades.Publicacion;
import tuti.desi.entidades.EstadoPublicacion;

public interface PublicacionService {

    List<Publicacion> buscarConFiltros(Long propiedadId, EstadoPublicacion estado, Double precioMin, Double precioMax);
    
    boolean existePublicacionActivaParaPropiedad(Long id);
    
    void eliminarLogicamente(Long id);
    
    // devuelve Publicacion y el parámetro se llama publicacionForm
    Publicacion guardar(Publicacion publicacionForm);
    
    Publicacion buscarPorId(Long id);
    
    List<Publicacion> obtenerTodas();
}