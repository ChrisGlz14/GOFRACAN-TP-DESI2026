package tuti.desi.servicios;

import java.util.List;

import tuti.desi.entidades.Factura;
import tuti.desi.entidades.HistorialEstadoFactura;

// Interface del servicio de historial de estados de factura
public interface HistorialEstadoFacturaService {

	// Guardo un registro en el historial
	void save(HistorialEstadoFactura historial);

	// Traigo todo el historial de una factura específico
	List<HistorialEstadoFactura> getHistorialByFactura(Factura factura);
}
