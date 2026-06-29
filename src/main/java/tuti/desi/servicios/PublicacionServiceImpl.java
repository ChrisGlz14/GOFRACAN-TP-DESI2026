package tuti.desi.servicios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IHistorialEstadoPublicacionRepo; // Importamos tu nuevo repo
import tuti.desi.accesoDatos.IPublicacionRepo;
import tuti.desi.entidades.EstadoPublicacion;
import tuti.desi.entidades.HistorialEstadoPublicacion;
import tuti.desi.entidades.Publicacion;

@Service
public class PublicacionServiceImpl implements PublicacionService { 

    @Autowired
    private IPublicacionRepo publicacionRepo; 
    
    @Autowired
    private IHistorialEstadoPublicacionRepo historialRepo; // Inyecto el repositorio del historial

    @Override
    public List<Publicacion> buscarConFiltros(Long propiedadId, EstadoPublicacion estado, Double precioMin, Double precioMax) {
        // Transformo los Double que vienen de la pantalla a BigDecimal con los nulls
        BigDecimal min = (precioMin != null) ? BigDecimal.valueOf(precioMin) : null;
        BigDecimal max = (precioMax != null) ? BigDecimal.valueOf(precioMax) : null;
        
        // va al repositorio
        return publicacionRepo.buscarConFiltros(propiedadId, estado, min, max);
    }

   
    public Publicacion guardar(Publicacion publicacionForm) {
        
        // CRITERIO DE ACEPTACIÓN: El precio mensual deberá seguir siendo un número positivo
        if (publicacionForm.getPrecioMensual() == null || publicacionForm.getPrecioMensual().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio mensual debe ser un número positivo.");
        }

        // Control si es una EDICIÓN (el ID ya existe en la base de datos)
        if (publicacionForm.getId() != null) {
            Optional<Publicacion> publicacionAnteriorOpt = publicacionRepo.findById(publicacionForm.getId());
            
            if (publicacionAnteriorOpt.isPresent()) {
                Publicacion publicBd = publicacionAnteriorOpt.get(); //ACÁ SE CREA LA VARIABLE QUE FALTA
                
                // CRITERIO DE ACEPTACIÓN: Las condiciones de alquiler podrán modificarse mientras la publicación no esté finalizada
                if (publicBd.getEstado() == EstadoPublicacion.FINALIZADA && !publicBd.getCondiciones().equals(publicacionForm.getCondiciones())) {
                    throw new RuntimeException("No se pueden modificar las condiciones de una publicación FINALIZADA.");
                }

                // Si en la pantalla se intenta cambiar o dejar el estado como ACTIVA
                if (publicacionForm.getEstado() == EstadoPublicacion.ACTIVA) {
                    boolean existeOtraActiva = publicacionRepo.existsByPropiedadIdAndEstadoAndEliminadaFalse(publicacionForm.getPropiedad().getId(), EstadoPublicacion.ACTIVA);
                    
                    if (existeOtraActiva && !publicBd.getId().equals(publicacionForm.getId())) {
                        throw new RuntimeException("Ya existe otra publicación ACTIVA para esta misma propiedad.");
                    }
                }

                // CRITERIO DE ACEPTACIÓN: Mantener el registro de cambios si el estado se editara 
                if (!publicBd.getEstado().equals(publicacionForm.getEstado())) {
                    HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion();
                    historial.setPublicacion(publicBd);
                    historial.setEstado(publicacionForm.getEstado()); // El nuevo estado
                    historial.setFechaHora(java.time.LocalDateTime.now()); // Fecha y hora actual
                    
                    // Guarda directo con el repositorio 
                    historialRepo.save(historial);
                    
                    if (publicBd.getHistorialEstados() != null) {
                        publicBd.getHistorialEstados().add(historial);
                    }
                }
                
                // Setea los cambios autorizados del formulario al objeto persistente de la BD
                publicBd.setPrecioMensual(publicacionForm.getPrecioMensual());
                publicBd.setCondiciones(publicacionForm.getCondiciones());
                publicBd.setDescripcion(publicacionForm.getDescripcion());
                publicBd.setEstado(publicacionForm.getEstado());
                publicBd.setFechaPublicacion(publicacionForm.getFechaPublicacion());
                
                return publicacionRepo.save(publicBd);
            }
        } else {
            // Control si es una PUBLICACIÓN NUEVA (ALTA de publicación)
            if (publicacionForm.getEstado() == EstadoPublicacion.ACTIVA) {
                boolean existeActiva = publicacionRepo.existsByPropiedadIdAndEstadoAndEliminadaFalse(publicacionForm.getPropiedad().getId(), EstadoPublicacion.ACTIVA);
                if (existeActiva) {
                    throw new RuntimeException("No se puede dar de alta como ACTIVA porque ya existe una publicación activa para esta propiedad.");
                }
            }
        }
        
        return publicacionRepo.save(publicacionForm);
    }

    @Override
    public Publicacion buscarPorId(Long id) {
        return publicacionRepo.findById(id).orElse(null);
    }

    @Override
    public boolean existePublicacionActivaParaPropiedad(Long id) {
        // Pasa de  EstadoPublicacion.ACTIVA en lugar del String "ACTIVA"
        return publicacionRepo.existsByPropiedadIdAndEstadoAndEliminadaFalse(id, EstadoPublicacion.ACTIVA);
    } 

    @Override
    public void eliminarLogicamente(Long id) {
        Publicacion publicacion = publicacionRepo.findById(id).orElse(null);
        if (publicacion != null) {
            publicacion.setEliminada(true); 
            publicacionRepo.save(publicacion); 
        }
    }
    
    @Override
    public List<Publicacion> obtenerTodas() {
        return publicacionRepo.findByEliminadaFalse();
    }
}