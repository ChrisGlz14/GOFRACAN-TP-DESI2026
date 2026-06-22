package tuti.desi.presentacion.propiedades;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tuti.desi.entidades.Ciudad;
import tuti.desi.entidades.EstadoPropiedad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.TipoPropiedad;
import tuti.desi.servicios.CiudadService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/propiedadBuscar")
public class PropiedadBuscarController {

    @Autowired
    private PropiedadService propiedadService;
    @Autowired
    private CiudadService ciudadService;

    @RequestMapping(method = RequestMethod.GET)
    public String preparaForm(Model modelo) {
        modelo.addAttribute("formBean", new PropiedadesBuscarForm());
        return "propiedad/propiedadBuscar";
    }

    // combos para filtrar
    @ModelAttribute("allCiudades")
    public List<Ciudad> getAllCiudades() {
        return ciudadService.getAll();
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
    public String submit(PropiedadesBuscarForm formBean, ModelMap modelo, @RequestParam String action) {

        if (action.equals("actionBuscar")) {
            List<Propiedad> propiedades = propiedadService.filter(formBean);
            modelo.addAttribute("formBean", formBean);
            modelo.addAttribute("resultados", propiedades);
            return "propiedad/propiedadBuscar";
        }

        if (action.equals("actionCancelar")) {
            modelo.clear();
            return "redirect:/";
        }

        if (action.equals("actionRegistrar")) {
            modelo.clear();
            return "redirect:/propiedadEditar";
        }

        return "redirect:/";
    }
}
