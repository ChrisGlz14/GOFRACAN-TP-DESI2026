package tuti.desi.presentacion.publicaciones;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import tuti.desi.entidades.HistorialEstadoPublicacion;
import tuti.desi.accesoDatos.IHistorialEstadoPublicacionRepo;
import tuti.desi.servicios.PropiedadService;
import tuti.desi.servicios.PublicacionService;

@Controller
@RequestMapping 
public class PublicacionesController {

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private PropiedadService propiedadService; 

    @Autowired
    private IHistorialEstadoPublicacionRepo historialRepo;

    // 1. Pantalla principal adaptada a la URL del grupo
    @GetMapping("/publicacionesBuscar") 
    public String listarPublicaciones(
            @RequestParam(value = "propiedadId", required = false) Long propiedadId,
            @RequestParam(value = "estado", required = false) EstadoPublicacion estado,
            @RequestParam(value = "precioMin", required = false) Double precioMin,
            @RequestParam(value = "precioMax", required = false) Double precioMax,
            Model model) {
        
        List<Publicacion> lista = publicacionService.buscarConFiltros(propiedadId, estado, precioMin, precioMax);
        model.addAttribute("publicaciones", lista);
        
        model.addAttribute("propiedades", propiedadService.obtenerDisponibles());
        model.addAttribute("estados", EstadoPublicacion.values()); 
        
        return "publicacion/publicacionesBuscar"; // Devuelve html
    }

    // 2. Pantalla de NUEVA PUBLICACIÓN 
    @GetMapping("/publicaciones/nueva") 
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("publicacion", new Publicacion()); 
        model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles()); 
        return "publicacion/publicacionEditar";
    }

    // 3. Pantalla de EDITAR 
    @GetMapping("/publicaciones/editar")
    public String mostrarFormularioEditar(@RequestParam("id") Long id, Model model) {
        Publicacion publicacionExistente = publicacionService.buscarPorId(id);
        model.addAttribute("publicacion", publicacionExistente); 
        model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles());
        return "publicacion/publicacionEditar";
    }

    // 4. Procesar el botón GUARDAR PUBLICACIÓN
    @PostMapping("/publicaciones/guardar")
    public String guardarPublicacion(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "precioMensual", required = false) Double precioMensual, 
            @RequestParam(value = "condiciones", required = false) String condiciones,     
            @RequestParam(value = "descripcion", required = false) String descripcion,     
            @RequestParam(value = "estadoForm", required = false) String estadoForm, 
            @RequestParam(value = "propiedadIdForm", required = false) Long propiedadIdForm, 
            Model model) {
        
        if (propiedadIdForm == null) {
            propiedadIdForm = 1L;
        }

        Publicacion publicacion;
        boolean registrarHistorial = false;
        EstadoPublicacion estadoNuevo = null;

        if (id != null) {
            publicacion = publicacionService.buscarPorId(id);
            if (publicacion == null) {
                publicacion = new Publicacion();
                publicacion.setId(id);
                publicacion.setFechaPublicacion(java.time.LocalDate.now());
            } else {
                if (estadoForm != null && !estadoForm.isEmpty()) {
                    estadoNuevo = EstadoPublicacion.valueOf(estadoForm);
                    if (!publicacion.getEstado().equals(estadoNuevo)) {
                        registrarHistorial = true; 
                    }
                }
            }
        } else {
            publicacion = new Publicacion();
            publicacion.setFechaPublicacion(java.time.LocalDate.now());
        }
        
        if (precioMensual != null) {
            publicacion.setPrecioMensual(BigDecimal.valueOf(precioMensual));
        } else if (publicacion.getPrecioMensual() == null) {
            publicacion.setPrecioMensual(BigDecimal.ZERO);
        }
        
        publicacion.setCondiciones(condiciones != null ? condiciones : "");        publicacion.setCondiciones(condiciones != null ? condiciones : "");
        publicacion.setDescripcion(descripcion != null ? descripcion : "");
        publicacion.setEliminada(false);
        
        if (estadoForm != null && !estadoForm.isEmpty()) {
            publicacion.setEstado(EstadoPublicacion.valueOf(estadoForm));
        } else if (publicacion.getEstado() == null) {
            publicacion.setEstado(EstadoPublicacion.ACTIVA);
        }

        tuti.desi.entidades.Propiedad propReal = propiedadService.buscarPorId(propiedadIdForm);
        if (propReal == null) {
            propReal = new tuti.desi.entidades.Propiedad();
            propReal.setId(propiedadIdForm);
        }
        publicacion.setPropiedad(propReal);

        if (EstadoPublicacion.ACTIVA.equals(publicacion.getEstado())) {
            boolean yaExisteActiva = false;
            try {
                yaExisteActiva = publicacionService.existePublicacionActivaParaPropiedad(propiedadIdForm);
            } catch (Exception e) {
                yaExisteActiva = false; 
            }
            
            if (yaExisteActiva && (id == null)) {
                model.addAttribute("error", "Ya existe una publicación ACTIVA para esta propiedad. Debe pausarla o finalizarla primero.");
                model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles());
                model.addAttribute("publicacion", publicacion);
                return "publicacion/publicacionEditar";
            }
        }

        publicacionService.guardar(publicacion);

        if (registrarHistorial) {
            HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion();
            historial.setPublicacion(publicacion);
            historial.setEstado(estadoNuevo);
            historial.setFechaHora(LocalDateTime.now());
            
            historialRepo.save(historial);
        }
        
        return "redirect:/publicacionesBuscar"; //  redirige de vuelta a la lista simétrica
    }

    // 5. Procesar la ELIMINACIÓN LÓGICA 
    @GetMapping("/publicaciones/eliminar/{id}")
    public String eliminarPublicacion(@PathVariable("id") Long id, Model model) {
        Publicacion publicacion = publicacionService.buscarPorId(id);
        
        if (publicacion != null) {
            if (publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
                model.addAttribute("error", "No se puede eliminar la publicación. Solo se permiten eliminar publicaciones en estado ACTIVA.");
                model.addAttribute("publicaciones", publicacionService.obtenerTodas());
                return "publicacion/publicacionesBuscar";
            }
            publicacionService.eliminarLogicamente(id);
        }
        
        return "redirect:/publicacionesBuscar"; 
}}