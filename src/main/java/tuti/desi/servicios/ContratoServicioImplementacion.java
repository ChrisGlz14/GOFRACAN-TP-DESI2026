package tuti.desi.servicios;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuti.desi.accesoDatos.IContratoRepo;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;

@Service
public class ContratoServicioImplementacion implements ContratoServicio {

    @Autowired
    IContratoRepo repo;

    @Override
    public List<Contrato> getAll() {
        return repo.findByEliminadoFalse();
    }

    @Override
    public Contrato getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("el contrato", id));
    }

    @Override
    public void save(Contrato contrato) throws Excepcion {
        if (contrato.getImporteMensual() == null ||
            contrato.getImporteMensual().signum() <= 0) {
            throw new Excepcion("El importe mensual debe ser positivo");
        }
        if (contrato.getDuracionMeses() == null || contrato.getDuracionMeses() <= 0) {
            throw new Excepcion("La duración en meses debe ser positiva");
        }
        if (contrato.getDiaVencimientoMensual() == null ||
            contrato.getDiaVencimientoMensual() < 1 ||
            contrato.getDiaVencimientoMensual() > 31) {
            throw new Excepcion("El día de vencimiento debe estar entre 1 y 31");
        }

        if (contrato.getId() == null) {
            contrato.setEstado(EstadoContrato.borrador);
        }

        repo.save(contrato);
    }

    @Override
    public void deleteById(Long id) {
        Contrato contrato = getById(id);
        contrato.setEliminado(true);
        repo.save(contrato);
    }
}