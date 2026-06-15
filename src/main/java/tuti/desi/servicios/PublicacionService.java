
	package tuti.desi.servicios;

	import java.util.List;

import org.jspecify.annotations.Nullable;

import tuti.desi.entidades.Publicacion;

	public interface PublicacionService {
		List<Publicacion> buscarConFiltros(Long propiedadId, tuti.desi.entidades.EstadoPublicacion estado, Double precioMin, Double precioMax);
		boolean existePublicacionActivaParaPropiedad(Long id);
		void eliminarLogicamente(Long id);
		Publicacion guardar(Publicacion publicacion);
		Publicacion buscarPorId(Long id);
		@Nullable
		Object obtenerTodas();
	}
