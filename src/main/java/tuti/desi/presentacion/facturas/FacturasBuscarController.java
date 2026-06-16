package tuti.desi.presentacion.facturas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.Persona;
import tuti.desi.servicios.FacturaService;
import tuti.desi.servicios.PersonaService;

@Controller
@RequestMapping("/facturasBuscar")
public class FacturasBuscarController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PersonaService personaService;

    @RequestMapping(method = RequestMethod.GET)
    public String preparaForm(Model modelo) {
        modelo.addAttribute("formBean", new FacturasBuscarForm());
        return "facturasBuscar";
    }

    @ModelAttribute("allEstados")
    public EstadoFactura[] getAllEstados() {
        return EstadoFactura.values();
    }

    @ModelAttribute("allPersonas")
    public List<Persona> getAllPersonas() {
        return personaService.getAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(FacturasBuscarForm formBean, BindingResult result,
                         ModelMap modelo, @RequestParam String action) {

        if ("Buscar".equals(action)) {
            List<Factura> resultados = facturaService.filtrar(
                    formBean.getEstado(),
                    formBean.getConcepto(),
                    formBean.getFechaDesde(),
                    formBean.getFechaHasta(),
                    formBean.getIdPersona());

            modelo.addAttribute("formBean", formBean);
            modelo.addAttribute("resultados", resultados);
            return "facturasBuscar";
        }

        if ("Cancelar".equals(action)) {
            return "redirect:/";
        }

        if ("Registrar".equals(action)) {
            return "redirect:/facturasEditar";
        }

        return "redirect:/";
    }
}
