package tuti.desi.presentacion.contratos;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tuti.desi.entidades.EstadoContrato;
import tuti.desi.servicios.ContratoServicio;
import tuti.desi.servicios.PersonaService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/contratosBuscar")
public class ContratoBuscarController {

    @Autowired
    private ContratoServicio service;

    @Autowired
    private PersonaService personaService;
    
    @Autowired 
    private PropiedadService propiedadService;

    @GetMapping
    public String buscar(Model model,
    		@RequestParam(required = false) Long idPropiedad,
            @RequestParam(required = false) Long idInquilino,
            @RequestParam(required = false) EstadoContrato estado,
            @RequestParam(required = false) LocalDate fechaInicioDesde) {

        model.addAttribute("contratos", service.buscar(idPropiedad, idInquilino, estado, fechaInicioDesde));
        model.addAttribute("allPersonas", personaService.getAll());
        model.addAttribute("allPropiedades", propiedadService.obtenerTodas());
        model.addAttribute("allEstados", EstadoContrato.values());
        model.addAttribute("idInquilino", idInquilino);
        model.addAttribute("estado", estado);
        model.addAttribute("fechaInicioDesde", fechaInicioDesde);

        return "contrato/contratosBuscar";
    }
}

