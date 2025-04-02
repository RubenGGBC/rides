package domain;

import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Valoracion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;
    private int estrellas;
    private String comentario;

    @ManyToOne
    private User usuario;

    @ManyToOne
    private Driver conductor;

    public Valoracion() { }

    public Valoracion(int estrellas, String comentario, User usuario, Driver conductor) {
        this.estrellas = estrellas;
        this.comentario = comentario;
        this.usuario = usuario;
        this.conductor = conductor;
    }

    // Getters y setters
    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Driver getConductor() {
        return conductor;
    }

    public void setConductor(Driver conductor) {
        this.conductor = conductor;
    }
}
