package tuti.desi.presentacion.publicaciones;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tuti.desi.entidades.Publicacion;
import tuti.desi.servicios.PublicacionService;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionesController {

    @Autowired
    private PublicacionService publicacionService;

    // 1. Esto se activa al entrar a: localhost:8080/publicaciones
    @GetMapping
    public String listarPublicaciones(Model model) {
        List<Publicacion> lista = publicacionService.obtenerTodas();
        model.addAttribute("publicaciones", lista);
        return "publicacionesBuscar"; // Abre la pantalla de búsqueda/listado
    }

    // 2. Esto se activa al entrar a: localhost:8080/publicaciones/editar
    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model) {
        // Dejamos preparado un objeto vacío para el formulario
        model.addAttribute("publicacion", new Publicacion()); 
        return "publicacionEditar"; // Abre la pantalla del formulario
    }
}