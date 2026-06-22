package tuti.desi.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.HistorialEstadoPublicacion;

@Repository
public interface IHistorialEstadoPublicacionRepo extends JpaRepository<HistorialEstadoPublicacion, Long> {
    // Listo, hereda todos los métodos de guardado automático de Spring Data
}