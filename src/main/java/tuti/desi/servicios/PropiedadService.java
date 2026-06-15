package tuti.desi.servicios;
import java.util.List;
import tuti.desi.entidades.Propiedad; // (O como se llame la entidad de Cris)



	public interface PropiedadService {
	    List<Propiedad> obtenerDisponibles();

		Propiedad buscarPorId(Long propiedadIdForm); 
	}	

