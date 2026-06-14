package tuti.desi.presentacion.publicaciones;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tuti.desi.entidades.Publicacion;
import tuti.desi.servicios.PublicacionService;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionesController {

    @Autowired
    private PublicacionService publicacionService;

    // 1. Pantalla principal de BUSCAR / LISTAR
    @GetMapping
    public String listarPublicaciones(Model model) {
        List<Publicacion> lista = publicacionService.obtenerTodas();
        model.addAttribute("publicaciones", lista);
        return "publicacionesBuscar"; 
    }

    // 2. Pantalla de NUEVA PUBLICACIÓN (Con las propiedades de prueba)
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("publicacion", new Publicacion()); 
        
        java.util.ArrayList<tuti.desi.entidades.Propiedad> listaFicticia = new java.util.ArrayList<>();
        
        tuti.desi.entidades.Propiedad p1 = new tuti.desi.entidades.Propiedad();
        p1.setId(1L);
        p1.setDireccion("Boulevar Gálvez 1500");
        listaFicticia.add(p1);
        
        tuti.desi.entidades.Propiedad p2 = new tuti.desi.entidades.Propiedad();
        p2.setId(2L);
        p2.setDireccion("General López 2800");
        listaFicticia.add(p2);
        
        model.addAttribute("propiedadesDisponibles", listaFicticia); 
        
        return "publicacionEditar"; 
    }

    // 3. Pantalla de EDITAR (Por ahora vacía como la tenían antes)
    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model) {
        model.addAttribute("publicacion", new Publicacion()); 
        return "publicacionEditar"; 
    }

    // 4. Procesar el botón GUARDAR PUBLICACIÓN
    @PostMapping("/guardar")
    public String guardarPublicacion(
            @RequestParam("propiedad") Long propiedadId,
            @RequestParam("precioMensual") java.math.BigDecimal precioMensual,
            @RequestParam("condiciones") String condiciones,
            @RequestParam("descripcion") String descripcion) {
        
        // 1. Creamos el cascarón de la Propiedad con el ID que viene desde la web
        tuti.desi.entidades.Propiedad propiedadAux = new tuti.desi.entidades.Propiedad();
        propiedadAux.setId(propiedadId);
            
        // 2. Creamos tu Publicación y le cargamos los datos reales del formulario
        Publicacion nuevaPublicacion = new Publicacion();
        nuevaPublicacion.setPrecioMensual(precioMensual);
        nuevaPublicacion.setCondiciones(condiciones); // Cargamos condiciones
        nuevaPublicacion.setDescripcion(descripcion);
        nuevaPublicacion.setFechaPublicacion(java.time.LocalDate.now()); 
        nuevaPublicacion.setEliminada(false);
        
        // 3. Le pasamos el ENUM correcto de tu paquete entidades
        nuevaPublicacion.setEstado(tuti.desi.entidades.EstadoPublicacion.ACTIVA); 
        
        // 4. Vinculamos la propiedad auxiliar a la publicación
        nuevaPublicacion.setPropiedad(propiedadAux);
        
        // 5. Guardamos en la base de datos a través de tu servicio
        publicacionService.guardar(nuevaPublicacion); 
        
        // 6. Redirigimos a la tabla principal
        return "redirect:/publicaciones"; 
    }
}