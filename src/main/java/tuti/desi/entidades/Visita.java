package tuti.desi.entidades;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;



	@Entity
	@Table(name = "visita")
	public class Visita {	

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private LocalDate fecha;

	    @Column(nullable = false)
	    private LocalTime hora;

	    @Column(length = 255)
	    private String comentario;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "estado_visita", nullable = false)
	    private EstadoVisita estadoVisita;

	    // Relación con Publicación (Muchas visitas corresponden a una Publicación)
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "publicacion_id", nullable = false)
	    private Publicacion publicacion;

	    // Relación con Persona/Cliente (Muchas visitas pueden ser pedidas por la misma Persona)
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "persona_id", nullable = false)
	    private Persona persona;

	    // --- CONSTRUCTORES ---
	    public Visita() {
	    }

	    public Visita(LocalDate fecha, LocalTime hora, String comentario, EstadoVisita estadoVisita, Publicacion publicacion, Persona persona) {
	        this.fecha = fecha;
	        this.hora = hora;
	        this.comentario = comentario;
	        this.estadoVisita = estadoVisita;
	        this.publicacion = publicacion;
	        this.persona = persona;
	    }

	    // --- GETTERS Y SETTERS ---
	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public LocalDate getFecha() {
	        return fecha;
	    }

	    public void setFecha(LocalDate fecha) {
	        this.fecha = fecha;
	    }

	    public LocalTime getHour() {
	        return hora;
	    }

	    public void setHora(LocalTime hora) {
	        this.hora = hora;
	    }

	    public String getComentario() {
	        return comentario;
	    }

	    public void setComentario(String comentario) {
	        this.comentario = comentario;
	    }

	    public EstadoVisita getEstadoVisita() {
	        return estadoVisita;
	    }

	    public void setEstadoVisita(EstadoVisita estadoVisita) {
	        this.estadoVisita = estadoVisita;
	    }

	    public Publicacion getPublicacion() {
	        return publicacion;
	    }

	    public void setPublicacion(Publicacion publicacion) {
	        this.publicacion = publicacion;
	    }

	    public Persona getPersona() {
	        return persona;
	    }

	    public void setPersona(Persona persona) {
	        this.persona = persona;
	    }
	}


