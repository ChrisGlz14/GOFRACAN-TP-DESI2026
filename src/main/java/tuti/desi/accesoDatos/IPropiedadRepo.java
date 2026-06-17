package tuti.desi.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tuti.desi.entidades.Propiedad;

// Extender JpaRepository nos regala gratis: findAll(), findById(), save(), deleteById(), etc.
// No hace falta escribir el SQL: Spring Data lo genera solo.
@Repository
public interface IPropiedadRepo extends JpaRepository<Propiedad, Long> {

}
