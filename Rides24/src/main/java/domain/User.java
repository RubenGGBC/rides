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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlID
	@Id 
	private String email;
	private String pasword; 
	private boolean driver;
	private String nombre; 
	@OneToMany(fetch = FetchType.EAGER)
    private List<Ride> reservedRides;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private CuentaBancaria cuentabanca;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Monedero monedero;


	public User(String email, String pasword,boolean driver,String nombre) {
		this.email = email;
		this.pasword = pasword;
		this.driver = driver;
        this.reservedRides = new Vector<>();
		this.nombre = nombre;
		this.monedero = new Monedero(email + "_wallet");
	    this.monedero.setUser(this);
	}
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email; 
	}

	public String getpasword() {
		return pasword;
	}

	public void setpasword(String pasword) {
		this.pasword = pasword;
	}
	
	public boolean getdriver() {
		return driver;
	}

	public void setdriver(boolean driver) {
		this.driver = driver;
	}
	
	public String getnombre() {
		return nombre;
	}

	public void setnombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Ride> getReservedRides() { return reservedRides; }
    public void addReservedRide(Ride ride) { this.reservedRides.add(ride); }
    
    public CuentaBancaria getCuenta() {
    	return this.cuentabanca;
    }
    
    public void setCuenta(CuentaBancaria c) {
    	this.cuentabanca=c;
    }
    public Monedero getMonedero() {
        return monedero;
    }
    
    public void setMonedero(Monedero monedero) {
        this.monedero = monedero;
    }
    
    public boolean ingresarDineroEnMonedero(float cantidad) {
        if (monedero != null) {
            return monedero.ingresarDinero(cantidad);
        }
        return false;
    }
    
    public boolean retirarDineroDeMonedero(float cantidad) {
        if (monedero != null && monedero.tieneSaldoSuficiente(cantidad)) {
            return monedero.retirarDinero(cantidad);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Usuario [email=" + email + ", nombre=" + nombre + ", conductor=" + driver + "]";
    }
}
   
    


	

	


