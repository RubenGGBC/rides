package testOperations;

import configuration.ConfigXML;
import domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TestDataAccess {
    protected  EntityManager  db;
    protected  EntityManagerFactory emf;

    ConfigXML  c=ConfigXML.getInstance();


    public TestDataAccess()  {

        System.out.println("TestDataAccess created");

        //open();

    }


    public void open(){


        String fileName=c.getDbFilename();

        if (c.isDatabaseLocal()) {
            emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
            db = emf.createEntityManager();
        } else {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("javax.persistence.jdbc.user", c.getUser());
            properties.put("javax.persistence.jdbc.password", c.getPassword());

            emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);

            db = emf.createEntityManager();
        }
        System.out.println("TestDataAccess opened");


    }
    public void close(){
        db.close();
        System.out.println("TestDataAccess clsed");
    }

    //Metodo corregido de sonar Ruben H,C
    public boolean removeDriver(String driverEmail) {
        System.out.println(">> TestDataAccess: removeRide");
        Driver d = db.find(Driver.class, driverEmail);
        if (d!=null) {
            db.getTransaction().begin();
            db.remove(d);
            db.getTransaction().commit();
            return true;
        } else {
            return false;
        }

    }

    public Driver getDriver(String driverEmail) {
        System.out.println(">> TestDataAccess: getDriver "+driverEmail);
        Driver d = db.find(Driver.class, driverEmail);

        return d;
    }
    public Driver createDriver(String email, String name) {
        System.out.println(">> TestDataAccess: createDriver");
        Driver driver=null;
        db.getTransaction().begin();
        try {
            driver=new Driver(email,name);;
            db.persist(driver);
            db.getTransaction().commit();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return driver;
    }
    public boolean existDriver(String email) {
        return  db.find(Driver.class, email)!=null;


    }

    public Driver addDriverWithRide(String email, String name, String from, String to,  Date date, int nPlaces, float price) {
        System.out.println(">> TestDataAccess: addDriverWithRide");
        Driver driver=null;
        db.getTransaction().begin();
        try {
            driver = db.find(Driver.class, email);
            if (driver==null)
                driver=new Driver(email,name);
            driver.addRide(from, to, date, nPlaces, price);
            db.persist(driver);
            System.out.println("Stored: "+driver);
            db.getTransaction().commit();
            return driver;

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return driver;
    }


    public boolean existRide(String email, String from, String to, Date date) {
        System.out.println(">> TestDataAccess: existRide");
        Driver d = db.find(Driver.class, email);
        if (d!=null) {
            return d.doesRideExists(from, to, date);
        } else{
            return false;}
    }
    public Ride removeRide(String email, String from, String to, Date date ) {
        System.out.println(">> TestDataAccess: removeRide");
        Driver d = db.find(Driver.class, email);
        if (d!=null) {
            db.getTransaction().begin();
            Ride r= d.removeRide(from, to, date);
            db.getTransaction().commit();
            return r;

        } else {
            return null;
        }

    }
    public User getUser(String email) {
        return db.find(User.class, email);
    }
    public void nousuario(String email, String password, String name){
        db.persist(null);
    }
    public void crearUserconMonederoSinDinero(String email, String password, String name,int dinerocuenta){
        User prueba = new User(email, password, false, name);
        CuentaBancaria cuenta = new CuentaBancaria(email + "_cuenta");
        Monedero monedero = new Monedero(email + "_wallet");
        prueba.setCuenta(cuenta);
        prueba.getCuenta().setNumeroRandom(dinerocuenta);
        prueba.setMonedero(monedero);
        monedero.setUser(prueba);
        db.getTransaction().begin();
        db.persist(prueba);
        db.getTransaction().commit();
    }
    public void crearUsersinMonedero(String email, String password, String name, float dinero) {
        User prueba = new User(email, password, false, name);
        CuentaBancaria cuenta = new CuentaBancaria(email + "_cuenta");
        prueba.setCuenta(cuenta);
        prueba.getCuenta().setNumeroRandom(20);
        prueba.setMonedero(null);
        db.getTransaction().begin();
        db.persist(prueba);
        db.getTransaction().commit();
    }
    public void crearUsersinMonederoDineroEnCuenta(String email, String password, String name, int dinero) {
        User prueba = new User(email, password, false, name);
        CuentaBancaria cuenta = new CuentaBancaria(email + "_cuenta");
        prueba.setCuenta(cuenta);
        prueba.getCuenta().setNumeroRandom(dinero);
        prueba.setMonedero(null);
        db.getTransaction().begin();
        db.persist(prueba);
        db.getTransaction().commit();



    }


    public void removeUser(String email) {
        User user = db.find(User.class, email);
        if (user != null) {
            db.getTransaction().begin();
            // Con CascadeType.REMOVE, al borrar el User se borran automáticamente CuentaBancaria y Monedero
            db.remove(user);
            db.getTransaction().commit();
        }
    }

    public boolean existMonedero(String email) {
        User user = db.find(User.class, email);
        return user != null && user.getMonedero() != null;
    }

    public void crearUserSinMonedero(String email, String password, String name, int dineroCuenta) {
        User user = new User(email, password, false, name);
        CuentaBancaria cuenta = new CuentaBancaria(email + "_cuenta");
        user.setCuenta(cuenta);
        user.getCuenta().setNumeroRandom(dineroCuenta);
        user.setMonedero(null);
        db.getTransaction().begin();
        db.persist(user);
        db.getTransaction().commit();
    }

    public void crearUserConMonederoSaldoInsuficiente(String email, String password, String name, int dineroCuenta, float saldoMonedero) {
        User user = new User(email, password, false, name);
        CuentaBancaria cuenta = new CuentaBancaria(email + "_cuenta");
        Monedero monedero = new Monedero(email + "_wallet");
        user.setCuenta(cuenta);
        user.getCuenta().setNumeroRandom(dineroCuenta);
        user.setMonedero(monedero);
        monedero.setUser(user);
        monedero.setSaldo(saldoMonedero);
        db.getTransaction().begin();
        db.persist(user);
        db.getTransaction().commit();
    }

    public void crearUserConMonederoSaldoSuficiente(String email, String password, String name, int dineroCuenta, float saldoMonedero) {
        User user = new User(email, password, false, name);
        CuentaBancaria cuenta = new CuentaBancaria(email + "_cuenta");
        Monedero monedero = new Monedero(email + "_wallet");
        user.setCuenta(cuenta);
        user.getCuenta().setNumeroRandom(dineroCuenta);
        user.setMonedero(monedero);
        monedero.setUser(user);
        monedero.setSaldo(saldoMonedero);
        db.getTransaction().begin();
        db.persist(user);
        db.getTransaction().commit();
    }
}


