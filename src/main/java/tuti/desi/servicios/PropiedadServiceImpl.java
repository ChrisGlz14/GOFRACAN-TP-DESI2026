package tuti.desi.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IHistorialEstadoPropiedadRepo;
import tuti.desi.accesoDatos.IPropiedadRepo;
import tuti.desi.entidades.HistorialEstadoPropiedad;
import tuti.desi.entidades.Propiedad;
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
        return propiedadRepo.findAll();
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
}
