package tuti.desi.servicios;

import java.util.List;
import tuti.desi.entidades.Contrato;
import tuti.desi.excepciones.Excepcion;

public interface ContratoServicio {
	
	//aca estan operaciones que el sistema puede realizar sobre los contratos
	//es decir alta/baja/modific/listar contratos --> enunciado
	
    void save(Contrato c) throws Excepcion; // alta o modificacion 
    
    void deleteById(Long id); //baja
    
    Contrato getById(Long id);  //busca por id  
   
    List<Contrato> getAll(); //obtiene todos los contratos

}
