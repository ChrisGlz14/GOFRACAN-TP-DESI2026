package tuti.desi.accesoDatos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;


@Repository
public interface IContratoRepo extends JpaRepository<Contrato, Long> {

    //lista de contraros no eliminados
    List<Contrato> findByEliminadoFalse();

    // VERIFICACIÓN — para no crear dos contratos activos en la misma propiedad
   // @Query("SELECT c FROM Contrato c WHERE c.propiedad.id = :idPropiedad AND c.estado = 'activo'")
   // List<Contrato> findActivosByPropiedad(Long idPropiedad); 
    
    // Trae contratos por estado
    List<Contrato> findByEstado(EstadoContrato estado);

}