package tuti.desi.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tuti.desi.entidades.HistorialEstadoPropiedad;

// repo para ir guardando cada cambio de estado de una propiedad en el historial
@Repository
public interface IHistorialEstadoPropiedadRepo extends JpaRepository<HistorialEstadoPropiedad, Long> {

}
