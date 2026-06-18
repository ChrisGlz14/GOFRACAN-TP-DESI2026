package tuti.desi.presentacion.publicaciones;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime; // Importamos para la fecha del historial
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
import tuti.desi.entidades.HistorialEstadoPublicacion; // Importamos la entidad historial
import tuti.desi.accesoDatos.IHistorialEstadoPublicacionRepo; // Importamos tu nuevo repositorio
import tuti.desi.servicios.PropiedadService;
import tuti.desi.servicios.PublicacionService;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionesController {

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private PropiedadService propiedadService; 

    @Autowired
    private IHistorialEstadoPublicacionRepo historialRepo; // Inyectamos el repo para guardar directo

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

    // 2. Pantalla de NUEVA PUBLICACIÓN (HU 2.1)
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("publicacion", new Publicacion()); 
        
        // Cargamos las propiedades reales para el combo
        model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles()); 
        return "publicacionEditar"; 
    }

    // 3. Pantalla de EDITAR (HU 2.3 - Recuperación por Parámetro de ID)
    @GetMapping("/editar")
    public String mostrarFormularioEditar(@RequestParam("id") Long id, Model model) {
        // Buscamos la publicación real en la base de datos por su ID
        Publicacion publicacionExistente = publicacionService.buscarPorId(id);
        
        // Se la pasamos al modelo para que el HTML dibuje los datos cargados
        model.addAttribute("publicacion", publicacionExistente); 
        model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles());
        return "publicacionEditar"; 
    }

    // 4. Procesar el botón GUARDAR PUBLICACIÓN (Blindado con Inteligencia de Edición y Cátedra)
    @PostMapping("/guardar")
    public String guardarPublicacion(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "precioMensual", required = false) Double precioMensual, 
            @RequestParam(value = "condiciones", required = false) String condiciones,     
            @RequestParam(value = "descripcion", required = false) String descripcion,     
            @RequestParam(value = "estadoForm", required = false) String estadoForm, 
            @RequestParam(value = "propiedadIdForm", required = false) Long propiedadIdForm, 
            Model model) {
        
        // Salvavidas por si viaja vacío el id de propiedad
        if (propiedadIdForm == null) {
            propiedadIdForm = 1L;
        }

        Publicacion publicacion;
        boolean registrarHistorial = false;
        EstadoPublicacion estadoNuevo = null;

        // ESTRATEGIA DE ANALISTA: Si el ID existe, recuperamos el registro histórico de la base de datos
        if (id != null) {
            publicacion = publicacionService.buscarPorId(id);
            if (publicacion == null) {
                publicacion = new Publicacion();
                publicacion.setId(id);
                publicacion.setFechaPublicacion(java.time.LocalDate.now());
            } else {
                // 🚀 INTERCEPCIÓN DEL HISTORIAL: Verificamos el cambio ANTES de pisar el objeto
                if (estadoForm != null && !estadoForm.isEmpty()) {
                    estadoNuevo = EstadoPublicacion.valueOf(estadoForm);
                    // Si el estado que tiene en la BD es diferente al que viene de la pantalla...
                    if (!publicacion.getEstado().equals(estadoNuevo)) {
                        registrarHistorial = true; // Dejamos la bandera en true para grabar después
                    }
                }
            }
        } else {
            // Si el ID es null, es un ALTA real: creamos objeto en blanco y asignamos fecha de hoy
            publicacion = new Publicacion();
            publicacion.setFechaPublicacion(java.time.LocalDate.now());
        }
        
        // CÁTEDRA: Convertimos el Double del formulario a BigDecimal para la entidad
        if (precioMensual != null) {
            publicacion.setPrecioMensual(BigDecimal.valueOf(precioMensual));
        } else if (publicacion.getPrecioMensual() == null) {
            publicacion.setPrecioMensual(BigDecimal.ZERO);
        }
        
        publicacion.setCondiciones(condiciones != null ? condiciones : "");
        publicacion.setDescripcion(descripcion != null ? descripcion : "");
        publicacion.setEliminada(false);
        
        // Mapeo del Estado del combo dinámico del HTML al objeto
        if (estadoForm != null && !estadoForm.isEmpty()) {
            publicacion.setEstado(EstadoPublicacion.valueOf(estadoForm));
        } else if (publicacion.getEstado() == null) {
            publicacion.setEstado(EstadoPublicacion.ACTIVA); // Por defecto en el Alta
        }

        // Buscamos la Propiedad real en la base de datos gofracan
        tuti.desi.entidades.Propiedad propReal = propiedadService.buscarPorId(propiedadIdForm);
        if (propReal == null) {
            propReal = new tuti.desi.entidades.Propiedad();
            propReal.setId(propiedadIdForm);
        }
        publicacion.setPropiedad(propReal);

        // Regla de Negocio: No duplicar publicaciones activas
        if (EstadoPublicacion.ACTIVA.equals(publicacion.getEstado())) {
            boolean yaExisteActiva = false;
            try {
                yaExisteActiva = publicacionService.existePublicacionActivaParaPropiedad(propiedadIdForm);
            } catch (Exception e) {
                yaExisteActiva = false; 
            }
            
            // Bloqueamos solo si es un ALTA real. Si es edición del mismo registro, permitimos guardar.
            if (yaExisteActiva && (id == null)) {
                model.addAttribute("error", "Ya existe una publicación ACTIVA para esta propiedad. Debe pausarla o finalizarla primero.");
                model.addAttribute("propiedadesDisponibles", propiedadService.obtenerDisponibles());
                model.addAttribute("publicacion", publicacion);
                return "publicacionEditar";
            }
        }

        // Guardado final de la publicación en la base de datos
        publicacionService.guardar(publicacion);

        // 🚀 OBLIGAMOS A MYSQL A CREAR EL REGISTRO DEL HISTORIAL
        if (registrarHistorial) {
            HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion();
            historial.setPublicacion(publicacion);
            historial.setEstado(estadoNuevo); // El estado nuevo pedido por el diagrama
            historial.setFechaHora(LocalDateTime.now()); // Columna fecha_hora
            
            historialRepo.save(historial); // El insert directo infalible
        }
        
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