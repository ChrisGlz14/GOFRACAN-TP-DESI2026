package tuti.desi.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
	@Table(name = "propiedad")
	public class Propiedad {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String direccion;

	    public Propiedad() {}

	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }
	    public String getDireccion() { return direccion; }
	    public void setDireccion(String direccion) { this.direccion = direccion; }
}
