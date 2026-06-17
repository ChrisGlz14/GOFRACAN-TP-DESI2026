package tuti.desi.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IPropiedadRepo;
import tuti.desi.entidades.Propiedad;

// @Service: marca esta clase como la implementación que Spring va a inyectar
// cada vez que alguien pida un PropiedadService (como hace el controller con @Autowired).
@Service
public class PropiedadServiceImpl implements PropiedadService {

    @Autowired
    private IPropiedadRepo propiedadRepo;

    @Override
    public List<Propiedad> obtenerTodas() {
        return propiedadRepo.findAll();
    }

    @Override
    public Propiedad guardar(Propiedad propiedad) {
        return propiedadRepo.save(propiedad);
    }

    @Override
    public Propiedad buscarPorId(Long id) {
        return propiedadRepo.findById(id).orElse(null);
    }
}
