
	package tuti.desi.servicios;

	import java.util.List;
	import tuti.desi.entidades.Publicacion;

	public interface PublicacionService {
	    List<Publicacion> obtenerTodas();
	    Publicacion guardar(Publicacion publicacion);
	    Publicacion buscarPorId(Long id);
	}

