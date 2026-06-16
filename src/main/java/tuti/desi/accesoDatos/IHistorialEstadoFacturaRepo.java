package tuti.desi.accesoDatos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tuti.desi.entidades.Factura;
import tuti.desi.entidades.HistorialEstadoFactura;

// Este repositorio es para guardar y buscar el historial de cambios de estado de las facturas
@Repository
public interface IHistorialEstadoFacturaRepo extends JpaRepository<HistorialEstadoFactura, Long> {

	// Traigo todo el historial de una factura específica ordenado por fecha, del más reciente al más viejo
	@Query("SELECT h FROM HistorialEstadoFactura h WHERE h.factura = :factura ORDER BY h.fechaHoraCambio DESC")
	List<HistorialEstadoFactura> findByFacturaOrderByFechaDesc(@Param("factura") Factura factura);
}
