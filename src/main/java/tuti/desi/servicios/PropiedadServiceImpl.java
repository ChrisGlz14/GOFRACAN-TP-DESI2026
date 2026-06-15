package tuti.desi.servicios;


	import java.util.ArrayList;
	import java.util.List;
	import org.springframework.stereotype.Service;

import tuti.desi.entidades.Propiedad;

	@Service // <-- ESTO es lo que le va a dar el "OK" a Spring para arrancar
	public class PropiedadServiceImpl implements PropiedadService {

	    @Override
	    public List<tuti.desi.entidades.Propiedad> obtenerDisponibles() {
	        // Le devolvemos una listita vacía por ahora para que no falle el combo del HTML
	        return new ArrayList<>();
	    }

		@Override
		public Propiedad buscarPorId(Long propiedadIdForm) {
			// TODO Auto-generated method stub
			return null;
		}
	}

