package tuti.desi.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IHistorialEstadoPropiedadRepo;
import tuti.desi.accesoDatos.IPropiedadRepo;
import tuti.desi.entidades.HistorialEstadoPropiedad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;

// @Service: marca esta clase como la implementación que Spring va a inyectar
// cada vez que alguien pida un PropiedadService (como hace el controller con @Autowired).
@Service
public class PropiedadServiceImpl implements PropiedadService {

    @Autowired
    private IPropiedadRepo propiedadRepo;
    @Autowired
    private IHistorialEstadoPropiedadRepo historialRepo;

    @Override
    public List<Propiedad> obtenerTodas() {
        // solo las no eliminadas, asi las que estan dadas de baja no salen en el listado
        return propiedadRepo.findByEliminadaFalse();
    }

    @Override
    public Propiedad guardar(Propiedad propiedad) throws Excepcion {
        // no puede haber dos propiedades activas (cualquier estado menos INACTIVA) con la misma direccion y ciudad
        if (propiedad.getId() == null
                && !propiedadRepo.findActivasMismaDireccionYCiudad(propiedad.getDireccion(), propiedad.getCiudad().getId()).isEmpty()) {
            throw new Excepcion("Ya existe una propiedad activa con la misma dirección y ciudad");
        }

        boolean esAlta = propiedad.getId() == null;
        Propiedad guardada = propiedadRepo.save(propiedad);

        // dejo guardado en el historial el estado con el que se da de alta la propiedad
        if (esAlta) {
            historialRepo.save(new HistorialEstadoPropiedad(guardada, guardada.getEstado(), LocalDateTime.now()));
        }

        return guardada;
    }

    @Override
    public Propiedad buscarPorId(Long id) {
        return propiedadRepo.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) throws Excepcion {
        Propiedad propiedad = propiedadRepo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("la propiedad", id));

        // no se puede eliminar una propiedad con un contrato activo vigente
        if (propiedadRepo.contarContratosActivos(id) > 0) {
            throw new Excepcion("No se puede eliminar la propiedad porque tiene un contrato activo vigente");
        }

        // baja logica: solo la marco como eliminada, asi no pierdo publicaciones, contratos, etc.
        propiedad.setEliminada(true);
        propiedadRepo.save(propiedad);
    }
}
