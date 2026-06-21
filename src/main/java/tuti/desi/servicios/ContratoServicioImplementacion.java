//aca van los metodos y los mensajes de error

package tuti.desi.servicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IContratoRepo;
import tuti.desi.accesoDatos.IHistorialEstadoContratoRepo;
//import tuti.desi.accesoDatos.IPropiedadRepo;
import tuti.desi.entidades.EstadoPropiedad;
import tuti.desi.entidades.HistorialEstadoContrato;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;

@Service
public class ContratoServicioImplementacion implements ContratoServicio {

    @Autowired
    IContratoRepo repo;

    @Autowired
    IHistorialEstadoContratoRepo historialRepo;

   // @Autowired
   // IPropiedadRepo propiedadRepo;

    @Override
    public List<Contrato> getAll() {
        return repo.findByEliminadoFalse();
    }

    @Override
    public Contrato getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("el contrato", id));
    }

    @Override
    public void save(Contrato contrato) throws Excepcion {

        // --- Validaciones de datos básicas ---
        // (la mayoría ya las cubre @Valid en el form; las repetimos acá
        // como defensa en profundidad, por si el servicio se llama desde
        // otro lado que no sea el form/controller)
        if (contrato.getImporteMensual() == null || contrato.getImporteMensual().signum() <= 0) {
            throw new Excepcion("El importe mensual debe ser positivo");
        }
        if (contrato.getDuracionMeses() == null || contrato.getDuracionMeses() <= 0) {
            throw new Excepcion("La duración en meses debe ser positiva");
        }
        if (contrato.getDiaVencimientoMensual() == null
                || contrato.getDiaVencimientoMensual() < 1
                || contrato.getDiaVencimientoMensual() > 31) {
            throw new Excepcion("El día de vencimiento debe estar entre 1 y 31");
        }
       // if (contrato.getPropiedad() == null) {
        //    throw new Excepcion("Debe seleccionar una propiedad");
        //}

        boolean esAlta = (contrato.getId() == null);

        if (esAlta) {
            altaContrato(contrato);
        } else {
            modificarContrato(contrato);
        }
    }


    // ALTA

    private void altaContrato(Contrato contrato) {

        contrato.setEstado(EstadoContrato.borrador);
        repo.save(contrato);

        registrarHistorial(contrato, EstadoContrato.borrador);
    }

 
    // MODIFICACIÓN
    private void modificarContrato(Contrato contratoNuevo) throws Excepcion {

        Contrato contratoActual = getById(contratoNuevo.getId());

        EstadoContrato estadoActual = contratoActual.getEstado();
        EstadoContrato estadoNuevo = contratoNuevo.getEstado();

        validarTransicion(estadoActual, estadoNuevo);

        //validar disponibilidad de la propiedad
        boolean seActiva = (estadoActual != EstadoContrato.activo
                && estadoNuevo == EstadoContrato.activo);

        // PENDIENTE (depende de Propiedad, esperando merge):
        // if (seActiva) {
        //     validarPuedeActivarse(contratoNuevo);
        // }

        // guardar los cambios 
        contratoActual.setFechaInicio(contratoNuevo.getFechaInicio());
        contratoActual.setDuracionMeses(contratoNuevo.getDuracionMeses());
        contratoActual.setImporteMensual(contratoNuevo.getImporteMensual());
        contratoActual.setDiaVencimientoMensual(contratoNuevo.getDiaVencimientoMensual());
        contratoActual.setDescripcion(contratoNuevo.getDescripcion());
        contratoActual.setPropietario(contratoNuevo.getPropietario());
        contratoActual.setInquilino(contratoNuevo.getInquilino());
        contratoActual.setEstado(estadoNuevo);

        repo.save(contratoActual);

        // actualizar historial
        if (estadoActual != estadoNuevo) {
            registrarHistorial(contratoActual, estadoNuevo);
            // PENDIENTE (depende de Propiedad, esperando merge):
            // actualizarEstadoPropiedadSegunContrato(contratoActual, estadoActual, estadoNuevo);
        }
    }


      private void validarTransicion(EstadoContrato actual, EstadoContrato nuevo) throws Excepcion {

        if (actual == nuevo) {
            return;
        }

        boolean transicionValida =
                   (actual == EstadoContrato.borrador && nuevo == EstadoContrato.activo)
                || (actual == EstadoContrato.activo && nuevo == EstadoContrato.finalizado)
                || (actual == EstadoContrato.activo && nuevo == EstadoContrato.rescindido);

        if (!transicionValida) {
            throw new Excepcion(
                "No se puede cambiar el estado del contrato de '" + actual
                + "' a '" + nuevo + "'. Transiciones válidas: borrador->activo, "
                + "activo->finalizado, activo->rescindido.");
        }
    }

   
    /*  activar un contrato
     private void validarPuedeActivarse(Contrato contrato) throws Excepcion {

        Propiedad propiedad = contrato.getPropiedad();

        // Regla: no se podrá activar un contrato si la propiedad no
        // está disponible.
        if (propiedad.getEstado() != EstadoPropiedad.DISPONIBLE) {
            throw new Excepcion(
                "No se puede activar el contrato: la propiedad no está disponible "
                + "(estado actual: " + propiedad.getEstado() + ")");
        }

        // Regla: una propiedad no puede tener más de un contrato activo.
        Optional<Contrato> otroActivo = repo.findByPropiedad_IdAndEstadoAndEliminadoFalse(
                propiedad.getId(), EstadoContrato.activo);

        if (otroActivo.isPresent() && !otroActivo.get().getId().equals(contrato.getId())) {
            throw new Excepcion(
                "La propiedad ya tiene un contrato activo (id " + otroActivo.get().getId() + ")");
        }
    }

    
    private void actualizarEstadoPropiedadSegunContrato(
            Contrato contrato, EstadoContrato estadoViejo, EstadoContrato estadoNuevo) {

        Propiedad propiedad = contrato.getPropiedad();

        if (estadoNuevo == EstadoContrato.activo) {
            // Al activarse el contrato, la propiedad pasa a ALQUILADA
            propiedad.setEstado(EstadoPropiedad.ALQUILADA);
            propiedadRepo.save(propiedad);

        } else if (estadoViejo == EstadoContrato.activo
                && (estadoNuevo == EstadoContrato.finalizado
                    || estadoNuevo == EstadoContrato.rescindido)) {
            // Al finalizar/rescindir, la propiedad puede volver a DISPONIBLE.
            propiedad.setEstado(EstadoPropiedad.DISPONIBLE);
            propiedadRepo.save(propiedad);
        }
    }*/


    // Historial de estados

    private void registrarHistorial(Contrato contrato, EstadoContrato estado) {
        HistorialEstadoContrato registro = new HistorialEstadoContrato(contrato, estado);
        historialRepo.save(registro);
    }


    // BAJA 
 
    @Override
    public void deleteById(Long id) throws Excepcion {
        Contrato contrato = getById(id);

        // unicamente se pueden eliminar contratos en estado "borrador".
        if (contrato.getEstado() != EstadoContrato.borrador) {
            throw new Excepcion(
                "No se puede eliminar el contrato: solo se permite eliminar "
                + "contratos en estado 'borrador' (estado actual: "
                + contrato.getEstado() + ")");
        }

        // la baja no debe afectar el estado de la propiedad
        contrato.setEliminado(true);
        repo.save(contrato);
    }
    
    @Override
    public List<Contrato> buscar(Long idInquilino, EstadoContrato estado, LocalDate fechaInicioDesde) {

        List<Contrato> todos = repo.findByEliminadoFalse();
        List<Contrato> resultado = new ArrayList<>();

        for (Contrato c : todos) {
            if (idInquilino != null && !c.getInquilino().getId().equals(idInquilino)) {
                continue;
            }
            if (estado != null && c.getEstado() != estado) {
                continue;
            }
            if (fechaInicioDesde != null && c.getFechaInicio().isBefore(fechaInicioDesde)) {
                continue;
            }
            resultado.add(c);
        }

        return resultado;
    }
}