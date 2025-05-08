package domain;
import java.io.Serializable;
import java.util.Random;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CuentaBancaria {
 @Id
 private String numerotarjeta;
 private int numeroRandom;
    
   
 public CuentaBancaria() {
        Random rand = new Random();
        this.numeroRandom = rand.nextInt(1000);
    }
    
   
    public CuentaBancaria(String numero) {
        Random rand = new Random();
        this.numeroRandom = rand.nextInt(1000);
        this.numerotarjeta = numero;
    }
    
    public String getNumerotarjeta() {
        return numerotarjeta;
    }
    
    public void setNumerotarjeta(String numerotarjeta) {
        this.numerotarjeta = numerotarjeta;
    }
    
    public int getNumeroRandom() {
        return numeroRandom;
    }
    
    public void setNumeroRandom(int numeroRandom) {
        this.numeroRandom = numeroRandom;
    }
    
    /**
     * Genera un nuevo número aleatorio
     */
    public void generarNuevoNumeroRandom() {
        Random rand = new Random();
        this.numeroRandom = rand.nextInt(1000);
    }
    
    @Override
    public String toString() {
        return "CuentaBancaria [numerotarjeta=" + numerotarjeta + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CuentaBancaria that = (CuentaBancaria) obj;
        return numerotarjeta != null && numerotarjeta.equals(that.numerotarjeta);
    }
    
    @Override
    public int hashCode() {
        return numerotarjeta != null ? numerotarjeta.hashCode() : 0;
    }
}
