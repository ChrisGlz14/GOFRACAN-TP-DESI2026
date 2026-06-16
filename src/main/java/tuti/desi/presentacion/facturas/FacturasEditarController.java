package tuti.desi.presentacion.facturas;

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
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.MedioPago;
import tuti.desi.entidades.Persona;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.FacturaService;
import tuti.desi.servicios.PersonaService;

@Controller
@RequestMapping("/facturasEditar")
public class FacturasEditarController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PersonaService personaService;

    // ─── GET: formulario alta / edición ─────────────────────────────────────

    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.GET)
    public String preparaForm(Model modelo, @PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            Factura entity = facturaService.getFacturaById(id.get());
            modelo.addAttribute("formBean", new FacturaForm(entity));
        } else {
            modelo.addAttribute("formBean", new FacturaForm());
        }
        return "facturasEditar";
    }

    // ─── POST: baja lógica ───────────────────────────────────────────────────

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public String deleteFactura(Model modelo, @PathVariable("id") Long id) {
        try {
            facturaService.bajaLogica(id);
        } catch (Excepcion e) {
            modelo.addAttribute("errorGlobal", e.getMessage());
        }
        return "redirect:/facturasBuscar";
    }

    // ─── POST: cambio de estado ──────────────────────────────────────────────

    @RequestMapping(path = "/estado/{id}", method = RequestMethod.POST)
    public String cambiarEstado(@PathVariable("id") Long id,
                                @RequestParam("nuevoEstado") EstadoFactura nuevoEstado,
                                Model modelo) {
        try {
            facturaService.cambiarEstado(id, nuevoEstado);
        } catch (Excepcion e) {
            modelo.addAttribute("errorGlobal", e.getMessage());
        }
        return "redirect:/facturasBuscar";
    }

    // ─── POST: registrar pago ────────────────────────────────────────────────

    @RequestMapping(path = "/pagar/{id}", method = RequestMethod.POST)
    public String registrarPago(@PathVariable("id") Long id,
                                @ModelAttribute("formBean") FacturaForm formBean,
                                BindingResult result, ModelMap modelo) {
        try {
            facturaService.registrarPago(id,
                    formBean.getFechaPago(),
                    formBean.getMedioPago(),
                    formBean.getImportePagado());
            return "redirect:/facturasBuscar";
        } catch (Excepcion e) {
            if (e.getAtributo() == null) {
                result.addError(new ObjectError("globalError", e.getMessage()));
            } else {
                result.addError(new FieldError("formBean", e.getAtributo(), e.getMessage()));
            }
            // Recargar la factura original para mostrar el form de pago con el error
            Factura entity = facturaService.getFacturaById(id);
            modelo.addAttribute("formBean", new FacturaForm(entity));
            modelo.addAttribute("mostrarPago", true);
            return "facturasEditar";
        }
    }

    // ─── POST: guardar (alta / edición) ─────────────────────────────────────

    @RequestMapping(method = RequestMethod.POST)
    public String submit(@ModelAttribute("formBean") @Valid FacturaForm formBean,
                         BindingResult result, ModelMap modelo,
                         @RequestParam String action) {

        if ("Aceptar".equals(action)) {
            if (result.hasErrors()) {
                modelo.addAttribute("formBean", formBean);
                return "facturasEditar";
            }
            try {
                facturaService.save(formBean.toPojo(), formBean.getIdPersona());
                return "redirect:/facturasBuscar";
            } catch (Excepcion e) {
                if (e.getAtributo() == null) {
                    result.addError(new ObjectError("globalError", e.getMessage()));
                } else {
                    result.addError(new FieldError("formBean", e.getAtributo(), e.getMessage()));
                }
                modelo.addAttribute("formBean", formBean);
                return "facturasEditar";
            }
        }

        if ("Cancelar".equals(action)) {
            return "redirect:/facturasBuscar";
        }

        return "redirect:/";
    }

    // ─── Model attributes ────────────────────────────────────────────────────

    @ModelAttribute("allEstados")
    public EstadoFactura[] getAllEstados() {
        return EstadoFactura.values();
    }

    @ModelAttribute("allMediosPago")
    public MedioPago[] getAllMediosPago() {
        return MedioPago.values();
    }

    @ModelAttribute("allPersonas")
    public List<Persona> getAllPersonas() {
        return personaService.getAll();
    }
}
