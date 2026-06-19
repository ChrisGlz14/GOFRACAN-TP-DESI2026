package tuti.desi.accesoDatos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;

@Repository
public interface IContratoRepo extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEliminadoFalse(); //lista de contraros no eliminados

    List<Contrato> findByEstadoAndEliminadoFalse(EstadoContrato estado);   // Trae contratos por estado

}