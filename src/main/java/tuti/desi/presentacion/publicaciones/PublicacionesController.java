package tuti.desi.presentacion.publicaciones;

import java.util.List;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tuti.desi.entidades.Publicacion;
import tuti.desi.entidades.EstadoPublicacion;
import tuti.desi.servicios.PropiedadService;
import tuti.desi.servicios.PublicacionService;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionesController {

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private PropiedadService propiedadService; 

    // 1. Pantalla principal de BUSCAR / LISTAR (HU 2.4 con Filtros Sincronizados)
    @GetMapping
    public String listarPublicaciones(
            @RequestParam(value = "propiedadId", required = false) Long propiedadId,
            @RequestParam(value = "estado", required = false) EstadoPublicacion estado,
            @RequestParam(value = "precioMin", required = false) Double precioMin,
            @RequestParam(value = "precioMax", required = false) Double precioMax,
            Model model) {
        
        List<Publicacion> lista = publicacionService.buscarConFiltros(propiedadId, estado, precioMin, precioMax);
        model.addAttribute("publicaciones", lista);
        
        // Combos para los filtros de la pantalla principal
        model.addAttribute("propiedades", propiedadService.obtenerDisponibles());
        model.addAttribute("estados", EstadoPublicacion.values()); 
        
        return "publicacionesBuscar"; 
    }

    // 2. Pantalla de NUEVA PUBLICACIÓN
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("publicacion", new Publicacion()); 
        
        // Cargamos las propiedades reales de Cristian para el combo
        model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles()); 
        return "publicacionEditar"; 
    }

    // 3. Pantalla de EDITAR
    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model) {
        model.addAttribute("publicacion", new Publicacion()); 
        model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles());
        return "publicacionEditar"; 
    }

    // 4. Procesar el botón GUARDAR PUBLICACIÓN (Blindado con BigDecimal para la cátedra)
    @PostMapping("/guardar")
    public String guardarPublicacion(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "precioMensual", required = false) Double precioMensual, 
            @RequestParam(value = "condiciones", required = false) String condiciones,     
            @RequestParam(value = "descripcion", required = false) String descripcion,     
            @RequestParam(value = "estado", required = false) String estadoForm,
            @RequestParam(value = "propiedadIdForm", required = false) Long propiedadIdForm, 
            Model model) {
        
        // Salvavidas por si viaja vacío
        if (propiedadIdForm == null) {
            propiedadIdForm = 1L;
        }

        Publicacion publicacion = new Publicacion();
        if (id != null) {
            publicacion.setId(id);
        }
        
        // CÁTEDRA: Convertimos el Double del formulario a BigDecimal para la entidad
        if (precioMensual != null) {
            publicacion.setPrecioMensual(BigDecimal.valueOf(precioMensual));
        } else {
            publicacion.setPrecioMensual(BigDecimal.ZERO);
        }
        
        publicacion.setCondiciones(condiciones != null ? condiciones : "");
        publicacion.setDescripcion(descripcion != null ? descripcion : "");
        publicacion.setEliminada(false);
        publicacion.setFechaPublicacion(java.time.LocalDate.now());
        
        // Mapeo del Estado
        if (estadoForm != null && !estadoForm.isEmpty()) {
            publicacion.setEstado(EstadoPublicacion.valueOf(estadoForm));
        } else {
            publicacion.setEstado(EstadoPublicacion.ACTIVA);
        }

        // Buscamos la Propiedad real en la base de datos gofracan
        tuti.desi.entidades.Propiedad propReal = propiedadService.buscarPorId(propiedadIdForm);
        if (propReal == null) {
            propReal = new tuti.desi.entidades.Propiedad();
            propReal.setId(propiedadIdForm);
        }
        publicacion.setPropiedad(propReal);

        // Regla de Negocio
        if (EstadoPublicacion.ACTIVA.equals(publicacion.getEstado())) {
            boolean yaExisteActiva = publicacionService.existePublicacionActivaParaPropiedad(propiedadIdForm);
            
            if (yaExisteActiva && (id == null)) { 
                model.addAttribute("error", "Ya existe una publicación ACTIVA para esta propiedad. Debe pausarla o finalizarla primero.");
                model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles()); 
                model.addAttribute("publicacion", publicacion); 
                return "publicacionEditar"; 
            }
        }

        // Guardado final en la base de datos
        publicacionService.guardar(publicacion);
        
        return "redirect:/publicaciones"; 
    }

    // 5. Procesar la ELIMINACIÓN LÓGICA (HU 2.2)
    @GetMapping("/eliminar/{id}")
    public String eliminarPublicacion(@PathVariable("id") Long id, Model model) {
        Publicacion publicacion = publicacionService.buscarPorId(id);
        
        if (publicacion != null) {
            if (publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
                model.addAttribute("error", "No se puede eliminar la publicación. Solo se permiten eliminar publicaciones en estado ACTIVA.");
                model.addAttribute("publicaciones", publicacionService.obtenerTodas());
                return "publicacionesBuscar"; 
            }
            publicacionService.eliminarLogicamente(id);
        }
        
        return "redirect:/publicaciones"; 
    }
}