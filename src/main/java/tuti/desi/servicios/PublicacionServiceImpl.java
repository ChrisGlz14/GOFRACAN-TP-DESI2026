package tuti.desi.servicios;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tuti.desi.accesoDatos.IPublicacionRepo;
import tuti.desi.entidades.Publicacion;
import tuti.desi.entidades.EstadoPublicacion; 
import tuti.desi.entidades.HistorialEstadoPublicacion;

@Service
public class PublicacionServiceImpl implements PublicacionService { 

    @Autowired
    private IPublicacionRepo publicacionRepo; 

    @Override
    public List<Publicacion> buscarConFiltros(Long propiedadId, EstadoPublicacion estado, Double precioMin, Double precioMax) {
        return publicacionRepo.buscarConFiltros(propiedadId, estado, precioMin, precioMax);
    }

    @Override
    @Transactional
    public Publicacion guardar(Publicacion publicacionForm) {
        
        // 1. Si la publicación ya existe (es una edición), buscamos cómo estaba antes en la base de datos
        if (publicacionForm.getId() != null) {
            Optional<Publicacion> publicacionAnteriorOpt = publicacionRepo.findById(publicacionForm.getId());
            
            if (publicacionAnteriorOpt.isPresent()) {
                Publicacion publicacionAnterior = publicacionAnteriorOpt.get();
                
                // 2. REGLA DE NEGOCIO: Comparamos si el estado viejo es DISTINTO al estado nuevo
                if (!publicacionAnterior.getEstado().equals(publicacionForm.getEstado())) {
                    
                    // ¡Acá detectamos el cambio de estado! Creamos el registro para el historial
                    HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion();
                    historial.setPublicacion(publicacionForm);
                    historial.setEstadoAnterior(publicacionAnterior.getEstado());
                    historial.setEstadoNuevo(publicacionForm.getEstado());
                    
                    // CORREGIDO: Usamos LocalDateTime.now() para que encastre con tu entidad perfecta
                    historial.setFechaCambio(java.time.LocalDateTime.now()); 
                    
                    // Lo agregamos a la lista interna de la publicación (CascadeType.ALL lo guarda solo)
                    publicacionForm.getHistorialEstados().add(historial);
                }
            }
        }
        
        // 3. Finalmente, guardamos la publicación y devuelve el resultado unificado
        return publicacionRepo.save(publicacionForm);
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
            publicacion.setEliminada(true); 
            publicacionRepo.save(publicacion); 
        }
    }
    
    @Override
    public List<Publicacion> obtenerTodas() {
        return publicacionRepo.findByEliminadaFalse();
    }}
