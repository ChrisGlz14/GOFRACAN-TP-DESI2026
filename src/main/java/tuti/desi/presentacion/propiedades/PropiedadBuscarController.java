package tuti.desi.presentacion.propiedades;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tuti.desi.entidades.Propiedad;
import tuti.desi.servicios.PropiedadService;

// @Controller: le dice a Spring "esta clase atiende pedidos web".
// Sin esta anotación, Spring la ignora y NINGUNA ruta de acá existe (ese era tu 404).
@Controller
// @RequestMapping a nivel de clase: TODOS los métodos de acá cuelgan de /propiedadBuscar
@RequestMapping("/propiedadBuscar")
public class PropiedadBuscarController {

    // @Autowired: Spring nos "presta" (inyecta) el servicio ya creado.
    // No hacemos new PropiedadService(): de eso se encarga Spring.
    @Autowired
    private PropiedadService propiedadService;

    // ===== 1) GET =====
    // Se dispara cuando escribís la URL en el navegador: localhost:8080/propiedadBuscar
    // Solo prepara el formulario vacío y muestra la pantalla.
    @RequestMapping(method = RequestMethod.GET)
    public String preparaForm(Model modelo) {
        // Creamos el objeto que la vista espera bajo el nombre "formBean"
        // (en el HTML: th:object="${formBean}").
        PropiedadForm form = new PropiedadForm();
        modelo.addAttribute("formBean", form);

        // El String que devolvemos es el NOMBRE del template en /templates (sin .html).
        // "propiedadBuscar" -> templates/propiedadBuscar.html
        return "propiedad/propiedadBuscar";
    }

    // ===== 2) POST =====
    // Se dispara cuando el usuario aprieta un botón del formulario (method="post").
    // Spring rellena 'formBean' solo, mapeando cada <input th:field="*{...}"> a su setter.
    // @RequestParam String action -> lee el value del botón presionado (name="action").
    @RequestMapping(method = RequestMethod.POST)
    public String submit(PropiedadForm formBean, ModelMap modelo, @RequestParam String action) {

        // Botón "Buscar": traemos las propiedades y las mandamos a la tabla de resultados.
        if (action.equals("actionBuscar")) {
            List<Propiedad> propiedades = propiedadService.obtenerTodas();
            modelo.addAttribute("formBean", formBean);     // así no se borra lo tipeado
            modelo.addAttribute("resultados", propiedades); // en el HTML: th:each="p : ${resultados}"
            return "propiedad/propiedadBuscar";
        }

        // Botón "Cancelar": limpiamos y volvemos al inicio.
        if (action.equals("actionCancelar")) {
            modelo.clear();
            return "redirect:/";
        }

        // Botón "Registrar": redirige a la pantalla de alta/edición.
        if (action.equals("actionRegistrar")) {
            modelo.clear();
            return "redirect:/propiedadEditar";
        }

        // Por las dudas, cualquier otra cosa vuelve al inicio.
        return "redirect:/";
    }
}
