package tuti.desi.servicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tuti.desi.accesoDatos.IContratoRepo;
import tuti.desi.accesoDatos.IHistorialEstadoContratoRepo;
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

    @Autowired
    PropiedadService propiedadService;

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

        boolean seActiva = (estadoActual != EstadoContrato.activo
                && estadoNuevo == EstadoContrato.activo);

        if (seActiva) {
            validarPuedeActivarse(contratoNuevo);
        }

        contratoActual.setFechaInicio(contratoNuevo.getFechaInicio());
        contratoActual.setDuracionMeses(contratoNuevo.getDuracionMeses());
        contratoActual.setImporteMensual(contratoNuevo.getImporteMensual());
        contratoActual.setDiaVencimientoMensual(contratoNuevo.getDiaVencimientoMensual());
        contratoActual.setDescripcion(contratoNuevo.getDescripcion());
        contratoActual.setPropietario(contratoNuevo.getPropietario());
        contratoActual.setInquilino(contratoNuevo.getInquilino());
        contratoActual.setPropiedad(contratoNuevo.getPropiedad());
        contratoActual.setEstado(estadoNuevo);

        repo.save(contratoActual);

        if (estadoActual != estadoNuevo) {
            registrarHistorial(contratoActual, estadoNuevo);
            actualizarEstadoPropiedadSegunContrato(contratoActual, estadoActual, estadoNuevo);
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

    // Activar un contrato
    private void validarPuedeActivarse(Contrato contrato) throws Excepcion {

        Propiedad propiedad = contrato.getPropiedad();

        if (propiedad.getEstado() != EstadoPropiedad.DISPONIBLE) {
            throw new Excepcion(
                "No se puede activar el contrato: la propiedad no está disponible "
                + "(estado actual: " + propiedad.getEstado() + ")");
        }

        Optional<Contrato> otroActivo = repo.findByPropiedad_IdAndEstadoAndEliminadoFalse(
                propiedad.getId(), EstadoContrato.activo);

        if (otroActivo.isPresent() && !otroActivo.get().getId().equals(contrato.getId())) {
            throw new Excepcion(
                "La propiedad ya tiene un contrato activo (id " + otroActivo.get().getId() + ")");
        }
    }

    private void actualizarEstadoPropiedadSegunContrato(
            Contrato contrato, EstadoContrato estadoViejo, EstadoContrato estadoNuevo) throws Excepcion {

        Propiedad propiedad = contrato.getPropiedad();

        if (estadoNuevo == EstadoContrato.activo) {
            propiedad.setEstado(EstadoPropiedad.ALQUILADA);
            propiedadService.guardar(propiedad);

        } else if (estadoViejo == EstadoContrato.activo
                && (estadoNuevo == EstadoContrato.finalizado
                    || estadoNuevo == EstadoContrato.rescindido)) {
            propiedad.setEstado(EstadoPropiedad.DISPONIBLE);
            propiedadService.guardar(propiedad);
        }
    }

    // Historial de estados
    private void registrarHistorial(Contrato contrato, EstadoContrato estado) {
        HistorialEstadoContrato registro = new HistorialEstadoContrato(contrato, estado);
        historialRepo.save(registro);
    }

    // BAJA
    @Override
    public void deleteById(Long id) throws Excepcion {
        Contrato contrato = getById(id);

        if (contrato.getEstado() != EstadoContrato.borrador) {
            throw new Excepcion(
                "No se puede eliminar el contrato: solo se permite eliminar "
                + "contratos en estado 'borrador' (estado actual: "
                + contrato.getEstado() + ")");
        }

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

    @Override
    public boolean tieneContratoActivo(Long idPropiedad) {
        return repo.findByPropiedad_IdAndEstadoAndEliminadoFalse(idPropiedad, EstadoContrato.activo)
                .isPresent();
    }
}