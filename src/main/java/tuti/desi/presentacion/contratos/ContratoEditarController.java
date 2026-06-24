package tuti.desi.presentacion.contratos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.entidades.Persona;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.ContratoServicio;
import tuti.desi.servicios.PersonaService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/contratosEditar")
public class ContratoEditarController {

    @Autowired
    private ContratoServicio service;

    @Autowired
    private PersonaService personaService;
    
    @Autowired
    private PropiedadService propiedadService;
    
    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.GET)
    public String preparaForm(Model modelo,
                              @PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            Contrato entity = service.getById(id.get());
            ContratoForm form = new ContratoForm(entity);
            modelo.addAttribute("formBean", form);
            modelo.addAttribute("esAlta", false);
        } else {
            modelo.addAttribute("formBean", new ContratoForm());
            modelo.addAttribute("esAlta", true);
        }
        return "contrato/contratosEditar";
    }

    @ModelAttribute("allPersonas")
    public List<Persona> getAllPersonas() {
        return personaService.getAll();
    }
    @ModelAttribute("allPropiedades")
    public List<Propiedad> getAllPropiedades() {
    return propiedadService.obtenerTodas();
     }
    
    @ModelAttribute("allEstados")
    public EstadoContrato[] getAllEstados() {
        return EstadoContrato.values();
    }
   
    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.POST)
    public String submit(
            @ModelAttribute("formBean") @Valid ContratoForm formBean,
            BindingResult result,
            ModelMap modelo,
            @RequestParam String action) {

        if (action.equals("Aceptar")) {
            if (result.hasErrors()) {
                modelo.addAttribute("formBean", formBean);
                modelo.addAttribute("esAlta", formBean.getId() == null);
                return "contrato/contratosEditar";
            }
            try {
                Contrato contrato = formBean.toPojo();

                contrato.setPropiedad(
                        propiedadService.buscarPorId(formBean.getIdPropiedad()));
                contrato.setPropietario(
                        personaService.getPersonaById(formBean.getIdPropietario()));
                contrato.setInquilino(
                        personaService.getPersonaById(formBean.getIdInquilino()));

                service.save(contrato);
                return "redirect:/contratosBuscar";
            } catch (Excepcion e) {
                e.printStackTrace();
                modelo.addAttribute("error", e.getMessage());
                modelo.addAttribute("formBean", formBean);
                modelo.addAttribute("esAlta", formBean.getId() == null);
                return "contrato/contratosEditar";
            }
        }
        if (action.equals("Cancelar")) {
            modelo.clear();
            return "redirect:/contratosBuscar";
        }
        return "redirect:/";
    }
}
