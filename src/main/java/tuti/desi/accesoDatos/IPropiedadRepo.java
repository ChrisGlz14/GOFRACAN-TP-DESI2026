package tuti.desi.accesoDatos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tuti.desi.entidades.Propiedad;

// Extender JpaRepository nos regala gratis: findAll(), findById(), save(), deleteById(), etc.
// No hace falta escribir el SQL: Spring Data lo genera solo.
@Repository
public interface IPropiedadRepo extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByDireccion(String direccion);

    @Query("SELECT p FROM Propiedad p WHERE p.direccion = :direccion AND p.id <> :idDistintoDe")
    List<Propiedad> findByDireccionAndIdNot(String direccion, Long idDistintoDe);

    // solo las que no estan eliminadas, para el listado (una eliminada no tiene que aparecer obbvimente)
    List<Propiedad> findByEliminadaFalse();

    // propiedades activas (cualquiera menos INACTIVA) con la misma direccion y ciudad, sin contar las eliminadas.
    // la uso para no dejar dar de alta una propiedad activa repetida
    @Query("SELECT p FROM Propiedad p WHERE LOWER(p.direccion) = LOWER(:direccion) AND p.ciudad.id = :idCiudad AND p.estado <> tuti.desi.entidades.EstadoPropiedad.INACTIVA AND p.eliminada = false")
    List<Propiedad> findActivasMismaDireccionYCiudad(String direccion, Long idCiudad);

    // todavia no tengo la entidad Contrato en esta parte, asi que consulto la tabla contratos directo
    // para saber si la propiedad tiene un contrato activo vigente
    @Query(value = "SELECT COUNT(*) FROM contratos WHERE propiedad_id = :idPropiedad AND estado = 'ACTIVO' AND eliminado = false", nativeQuery = true)
    long contarContratosActivos(Long idPropiedad);

}
