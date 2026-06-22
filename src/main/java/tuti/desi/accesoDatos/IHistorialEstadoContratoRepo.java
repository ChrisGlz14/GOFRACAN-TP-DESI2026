package tuti.desi.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.HistorialEstadoContrato;

@Repository
public interface IHistorialEstadoContratoRepo extends JpaRepository<HistorialEstadoContrato, Long> {
}