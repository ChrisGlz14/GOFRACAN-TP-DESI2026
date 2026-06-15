package tuti.desi.servicios;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuti.desi.accesoDatos.IPublicacionRepo;
import tuti.desi.entidades.Publicacion;
import tuti.desi.entidades.EstadoPublicacion; // <-- AGREGADO: Para que reconozca el Enum

@Service
public class PublicacionServiceImpl implements PublicacionService { // <-- CORREGIDO: Sacamos el 'abstract'

    @Autowired
    private IPublicacionRepo publicacionRepo;

    @Override
    public List<Publicacion> buscarConFiltros(Long propiedadId, EstadoPublicacion estado, Double precioMin, Double precioMax) {
        // Llama directo al repositorio pasándole los 4 datos
        return publicacionRepo.buscarConFiltros(propiedadId, estado, precioMin, precioMax);
    }

    @Override
    public Publicacion guardar(Publicacion publicacion) {
        return publicacionRepo.save(publicacion);
    }

    @Override
    public Publicacion buscarPorId(Long id) {
        return publicacionRepo.findById(id).orElse(null);
    }

    @Override
    public boolean existePublicacionActivaParaPropiedad(Long id) {
        return publicacionRepo.existsByPropiedadIdAndEstadoAndEliminadaFalse(id, "ACTIVA");
    }

    @Override
    public void eliminarLogicamente(Long id) {
        Publicacion publicacion = publicacionRepo.findById(id).orElse(null);
        if (publicacion != null) {
            // Cambiamos el flag a true en vez de borrar el registro físico
            publicacion.setEliminada(true); 
            publicacionRepo.save(publicacion); // Impacta el cambio en la BD
        }
    }
    
    @Override
    public List<Publicacion> obtenerTodas() {
        return publicacionRepo.findByEliminadaFalse();
    }
}