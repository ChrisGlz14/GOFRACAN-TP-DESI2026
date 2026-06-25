package tuti.desi.accesoDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tuti.desi.entidades.HistorialEstadoPublicacion;

public interface IHistorialEstadoPublicación {
	

	@Repository
	public interface IHistorialEstadoPublicacionRepo extends JpaRepository<HistorialEstadoPublicacion, Long> {
	}
}
