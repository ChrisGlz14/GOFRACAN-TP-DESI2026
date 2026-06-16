package tuti.desi.accesoDatos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;

@Repository
public interface IFacturaRepo extends JpaRepository<Factura, Long> {

    @Query("SELECT f FROM Factura f LEFT JOIN FETCH f.persona WHERE f.eliminada = false ORDER BY f.fechaVencimiento DESC")
    List<Factura> findAllActivas();

    @Query("""
            SELECT f FROM Factura f LEFT JOIN FETCH f.persona p
            WHERE f.eliminada = false
              AND (:estado    IS NULL OR f.estado = :estado)
              AND (:concepto  IS NULL OR LOWER(f.concepto) LIKE LOWER(CONCAT('%', :concepto, '%')))
              AND (:fechaDesde IS NULL OR f.fechaVencimiento >= :fechaDesde)
              AND (:fechaHasta IS NULL OR f.fechaVencimiento <= :fechaHasta)
              AND (:idPersona  IS NULL OR p.id = :idPersona)
            ORDER BY f.fechaVencimiento DESC
            """)
    List<Factura> filtrar(
            @Param("estado")     EstadoFactura estado,
            @Param("concepto")   String concepto,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,
            @Param("idPersona")  Long idPersona);

    boolean existsByIdAndEliminadaFalse(Long id);
}
