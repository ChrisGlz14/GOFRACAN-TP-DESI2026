package tuti.desi.accesoDatos;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.Publicacion;
import tuti.desi.entidades.EstadoPublicacion;

@Repository
public interface IPublicacionRepo extends JpaRepository<Publicacion, Long> {
    
    boolean existsByPropiedadIdAndEstadoAndEliminadaFalse(Long propiedadId, EstadoPublicacion estado);

    List<Publicacion> findByEliminadaFalse();

    // El filtro de las variables reales de la HU
    @Query("SELECT p FROM Publicacion p WHERE p.eliminada = false " +
           "AND (:propiedadId IS NULL OR p.propiedad.id = :propiedadId) " +
           "AND (:estado IS NULL OR p.estado = :estado) " +
           "AND (:precioMin IS NULL OR p.precioMensual >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precioMensual <= :precioMax)")
    List<Publicacion> buscarConFiltros(
        @Param("propiedadId") Long propiedadId,
        @Param("estado") EstadoPublicacion estado,
        @Param("precioMin") BigDecimal precioMin,
        @Param("precioMax") BigDecimal precioMax
    );
}
