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

}
