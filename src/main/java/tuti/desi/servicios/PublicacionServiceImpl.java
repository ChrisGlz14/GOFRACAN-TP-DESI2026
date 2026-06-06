package tuti.desi.servicios;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuti.desi.accesoDatos.IPublicacionRepo;
import tuti.desi.entidades.Publicacion;

@Service
public class PublicacionServiceImpl implements PublicacionService {

    @Autowired
    private IPublicacionRepo publicacionRepo;

    @Override
    public List<Publicacion> obtenerTodas() {
        return publicacionRepo.findAll();
    }

    @Override
    public Publicacion guardar(Publicacion publicacion) {
        return publicacionRepo.save(publicacion);
    }

    @Override
    public Publicacion buscarPorId(Long id) {
        return publicacionRepo.findById(id).orElse(null);
    }
}