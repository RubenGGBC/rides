package domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;


@Entity
public class Monedero implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    private float saldo;
    
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private CuentaBancaria cuentaBancaria;
    
    @OneToOne(mappedBy = "monedero")
    private User user;
    @OneToOne(mappedBy = "monedero")
    private Driver d;
     
    public Driver getD() {
		return d;
	}

	public void setD(Driver d) {
		this.d = d;
	}

	
    public Monedero() {
        this.saldo = 0;
    }
  
    public Monedero(String id) {
        this.id = id;
        this.saldo = 0;
    }
    
    
    public Monedero(String id, CuentaBancaria cuentaBancaria) {
        this.id = id;
        this.saldo = 0;
        this.cuentaBancaria = cuentaBancaria;
    }
    
    public String getId() {
        return id;
    }
    
  
    public void setId(String id) {
        this.id = id;
    }
    
    
    public float getSaldo() {
        return saldo;
    }
    
    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
    
   
    public CuentaBancaria getCuentaBancaria() {
        return cuentaBancaria;
    }
    
  
    public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
   
    public boolean ingresarDinero(float cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        
        this.saldo += cantidad;
        return true;
    }
    
    
    public boolean retirarDinero(float cantidad) {
        if (cantidad <= 0 || cantidad > this.saldo) {
            return false;
        }
        
        this.saldo -= cantidad;
        return true;
    }
    
    
    public boolean tieneSaldoSuficiente(float cantidad) {
        return this.saldo >= cantidad;
    }
    
    @Override
    public String toString() {
        return "Monedero [id=" + id + ", saldo=" + saldo + "]";
    }


	}
