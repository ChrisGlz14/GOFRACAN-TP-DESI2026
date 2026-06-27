package tuti.desi.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IHistorialEstadoPropiedadRepo;
import tuti.desi.accesoDatos.IPropiedadRepo;
import tuti.desi.entidades.EstadoPropiedad;
import tuti.desi.entidades.HistorialEstadoPropiedad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.propiedades.PropiedadesBuscarForm;

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
    public List<Propiedad> obtenerDisponibles() {
        return propiedadRepo.findByEliminadaFalse();
    }

    @Override
    public List<Propiedad> filter(PropiedadesBuscarForm filter) {
        return propiedadRepo.filter(filter.getDireccion(), filter.getCiudadSeleccionada(), filter.getTipo(), filter.getEstado());
    }

    @Override
    public Propiedad guardar(Propiedad propiedad) throws Excepcion {
        boolean esAlta = propiedad.getId() == null;

        // no puede haber dos propiedades activas (cualquier estado menos INACTIVA) con la misma direccion y ciudad
        if (esAlta) {
            if (!propiedadRepo.findActivasMismaDireccionYCiudad(propiedad.getDireccion(), propiedad.getCiudad().getId()).isEmpty()) {
                throw new Excepcion("Ya existe una propiedad activa con la misma dirección y ciudad");
            }
        } else {
            if (!propiedadRepo.findOtraActivaMismaDireccionYCiudad(propiedad.getDireccion(), propiedad.getCiudad().getId(), propiedad.getId()).isEmpty()) {
                throw new Excepcion("Ya existe otra propiedad activa con la misma dirección y ciudad");
            }
        }

        // en una edicion necesito el estado que tenia antes para saber si despues cambio
        EstadoPropiedad estadoAnterior = null;
        if (!esAlta) {
            Propiedad actual = propiedadRepo.findById(propiedad.getId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("la propiedad", propiedad.getId()));
            estadoAnterior = actual.getEstado();

            // si tiene un contrato activo no se puede pasar a DISPONIBLE o INACTIVA sin finalizar o rescindir el contrato
            if (propiedadRepo.contarContratosActivos(propiedad.getId()) > 0
                    && (propiedad.getEstado() == EstadoPropiedad.DISPONIBLE || propiedad.getEstado() == EstadoPropiedad.INACTIVA)) {
                throw new Excepcion("No se puede cambiar el estado a DISPONIBLE o INACTIVA porque la propiedad tiene un contrato activo");
            }
        }

        Propiedad guardada = propiedadRepo.save(propiedad);

        // dejo registro en el historial si es un alta, o si en una edicion cambio el estado
        if (esAlta || estadoAnterior != guardada.getEstado()) {
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
