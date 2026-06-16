//BORRAR ESTO CUANDO CHRISTIAN SUBA SU PARTE!!!!!

package tuti.desi.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "propiedad")
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Propiedad() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}