package tuti.desi.presentacion.contratos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.ContratoServicio;

@Controller
@RequestMapping("/contratosEliminar")
public class ContratoEliminarController {

    @Autowired
    private ContratoServicio service;

    @GetMapping("/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
        } catch (Excepcion e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contratosBuscar";
    }
}
