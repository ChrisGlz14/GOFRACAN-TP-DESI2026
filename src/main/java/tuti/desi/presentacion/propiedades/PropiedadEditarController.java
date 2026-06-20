package tuti.desi.presentacion.propiedades;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import tuti.desi.entidades.Ciudad;
import tuti.desi.entidades.EstadoPropiedad;
import tuti.desi.entidades.Persona;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.TipoPropiedad;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.CiudadService;
import tuti.desi.servicios.PersonaService;
import tuti.desi.servicios.PropiedadService;

// pantalla de alta de una propiedad (HU 1.1), armado igual que CiudadRegistrarEditarController
@Controller
@RequestMapping("/propiedadEditar")
public class PropiedadEditarController {

    @Autowired
    private PropiedadService servicioPropiedad;
    @Autowired
    private CiudadService servicioCiudad;
    @Autowired
    private PersonaService servicioPersona;

    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.GET)
    public String preparaForm(Model modelo, @PathVariable("id") Optional<Long> id) throws Exception {
        if (id.isPresent()) {
            // si viene con id es una edicion, sino es un alta nueva
            Propiedad entity = servicioPropiedad.buscarPorId(id.get());
            modelo.addAttribute("formBean", new PropiedadForm(entity));
        } else {
            modelo.addAttribute("formBean", new PropiedadForm());
        }
        return "propiedad/propiedadEditar";
    }

    // lista de ciudades para el combo
    @ModelAttribute("allCiudades")
    public List<Ciudad> getAllCiudades() {
        return servicioCiudad.getAll();
    }

    // propietario: personas no eliminadas. como las personas se borran fisico de la bd, getAll() ya trae solo las que quedan
    @ModelAttribute("allPropietarios")
    public List<Persona> getAllPropietarios() {
        return servicioPersona.getAll();
    }

    @ModelAttribute("allTipos")
    public TipoPropiedad[] getAllTipos() {
        return TipoPropiedad.values();
    }

    @ModelAttribute("allEstados")
    public EstadoPropiedad[] getAllEstados() {
        return EstadoPropiedad.values();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(@ModelAttribute("formBean") @Valid PropiedadForm formBean, BindingResult result, ModelMap modelo, @RequestParam String action) {

        if (action.equals("actionAceptar")) {
            if (result.hasErrors()) {
                modelo.addAttribute("formBean", formBean);
                return "propiedad/propiedadEditar";
            }
            try {
                Propiedad p = formBean.toPojo();
                // cargo las relaciones con los ids que vienen de los combos
                p.setCiudad(servicioCiudad.getById(formBean.getIdCiudad()));
                p.setPropietario(servicioPersona.getPersonaById(formBean.getIdPropietario()));
                servicioPropiedad.guardar(p);
                return "redirect:/propiedadBuscar";
            } catch (Excepcion e) {
                if (e.getAtributo() == null) {
                    ObjectError error = new ObjectError("globalError", e.getMessage());
                    result.addError(error);
                } else {
                    FieldError error1 = new FieldError("formBean", e.getAtributo(), e.getMessage());
                    result.addError(error1);
                }
                modelo.addAttribute("formBean", formBean);
                // como hay un error me quedo en la misma pantalla
                return "propiedad/propiedadEditar";
            }
        } else if (action.equals("actionCancelar")) {
            modelo.clear();
            return "redirect:/propiedadBuscar";
        }

        return "redirect:/";
    }
}
