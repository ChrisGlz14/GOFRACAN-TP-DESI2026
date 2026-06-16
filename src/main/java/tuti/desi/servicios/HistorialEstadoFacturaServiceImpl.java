package tuti.desi.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IHistorialEstadoFacturaRepo;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.HistorialEstadoFactura;

// Implementación del servicio de historial de estados de factura
@Service
public class HistorialEstadoFacturaServiceImpl implements HistorialEstadoFacturaService {

	@Autowired
	private IHistorialEstadoFacturaRepo historialRepo;

	@Override
	public void save(HistorialEstadoFactura historial) {
		// Guardo el registro del historial en la base de datos
		historialRepo.save(historial);
	}

	@Override
	public List<HistorialEstadoFactura> getHistorialByFactura(Factura factura) {
		// Traigo todo el historial de la factura ordenado por fecha descendente (más reciente primero)
		return historialRepo.findByFacturaOrderByFechaDesc(factura);
	}
}
