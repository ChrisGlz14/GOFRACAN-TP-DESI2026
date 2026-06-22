package tuti.desi.accesoDatos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;

@Repository
public interface IContratoRepo extends JpaRepository<Contrato, Long>,
        JpaSpecificationExecutor<Contrato> {

    @Query("SELECT c FROM Contrato c WHERE c.eliminado = FALSE")
    List<Contrato> findByEliminadoFalse();
    List<Contrato> findByEstadoAndEliminadoFalse(EstadoContrato estado);
    Optional<Contrato> findByPropiedad_IdAndEstadoAndEliminadoFalse(Long propiedadId, EstadoContrato estado);

}