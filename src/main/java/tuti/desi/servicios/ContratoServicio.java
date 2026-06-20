package tuti.desi.servicios;

import java.time.LocalDate;
import java.util.List;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.excepciones.Excepcion;

public interface ContratoServicio {
	
	//aca estan operaciones que el sistema puede realizar sobre los contratos
	//es decir alta/baja/modific/listar contratos --> enunciado
	
    void save(Contrato c) throws Excepcion; // alta o modificacion 
    
    void deleteById(Long id) throws Excepcion; //baja
    
    Contrato getById(Long id);  //busca por id  
   
    List<Contrato> getAll(); //obtiene todos los contratos
    List<Contrato> buscar(Long idInquilino, EstadoContrato estado, LocalDate fechaInicioDesde);

}
