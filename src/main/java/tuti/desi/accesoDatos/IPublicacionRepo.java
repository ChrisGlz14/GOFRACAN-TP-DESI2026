package tuti.desi.accesoDatos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- AGREGADO
import org.springframework.data.repository.query.Param; // <-- AGREGADO
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.Publicacion;
import tuti.desi.entidades.EstadoPublicacion; // <-- AGREGADO (Asegurate de que este sea el paquete correcto de tu Enum)

@Repository
public interface IPublicacionRepo extends JpaRepository<Publicacion, Long> {
	
    boolean existsByPropiedadIdAndEstadoAndEliminadaFalse(Long propiedadId, String estado);

    List<Publicacion> findByEliminadaFalse();

 // El súper filtro de la HU 2.4 (Sin el atributo ciudad por ahora para que no rompa)
    @Query("SELECT p FROM Publicacion p WHERE p.eliminada = false " +
           "AND (:propiedadId IS NULL OR p.propiedad.id = :propiedadId) " +
           "AND (:estado IS NULL OR p.estado = :estado) " +
           "AND (:precioMin IS NULL OR p.precioMensual >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precioMensual <= :precioMax)")
    List<Publicacion> buscarConFiltros(
        @Param("propiedadId") Long propiedadId,
        @Param("estado") EstadoPublicacion estado,
        @Param("precioMin") Double precioMin,
        @Param("precioMax") Double precioMax
    );
}