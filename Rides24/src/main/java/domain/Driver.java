package domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Driver implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlID
    @Id 
    private String email;
    private String name; 
    @XmlIDREF
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Ride> rides = new Vector<>();
    
    public Driver() {
        super();
    }

    public Driver(String email, String name) {
        this.email = email;
        this.name = name;
    }
    
    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ride> getRides() {
        return rides;
    }

    /**
     * Agrega un nuevo viaje a la lista del conductor.
     */
    public Ride addRide(String from, String to, Date date, int nPlaces, float price)  {
        Ride ride = new Ride(from, to, date, nPlaces, price, this);
        rides.add(ride);
        return ride;
    }

    /**
     * Verifica si ya existe un viaje con los mismos parámetros.
     */
    public boolean doesRideExists(String from, String to, Date date)  {    
        for (Ride r : rides)
            if ((java.util.Objects.equals(r.getFrom(), from)) &&
                (java.util.Objects.equals(r.getTo(), to)) &&
                (java.util.Objects.equals(r.getDate(), date)))
                return true;
        return false;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Driver other = (Driver) obj;
        return java.util.Objects.equals(email, other.email);
    }

    /**
     * Elimina un viaje a partir de sus parámetros.
     */
    public Ride removeRide(String from, String to, Date date) {
        int index = 0;
        boolean found = false;
        Ride r = null;
        while (!found && index < rides.size()) {
            r = rides.get(index);
            if ((java.util.Objects.equals(r.getFrom(), from)) &&
                (java.util.Objects.equals(r.getTo(), to)) &&
                (java.util.Objects.equals(r.getDate(), date))) {
                found = true;
            } else {
                index++;
            }
        }
        if (found) {
            rides.remove(index);
            return r;
        } else {
            return null;
        }
    }
    

  
    
    @Override
    public String toString(){
        return email + ";" + name + rides;
    }
}
