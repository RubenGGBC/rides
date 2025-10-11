package dataAccess;

import java.io.File;
import java.net.NoRouteToHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.*;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.SaldoInsuficienteException;
import exceptions.UserAlredyExistException;
import exceptions.AnyRidesException;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.NonexitstenUserException;

/**
 * It implements the data access to the objectDb database
 */
public class DataAccess  {
	private static final EstadoViaje PENDIENTE = null;
	private static final String EMAIL_PARAMETER = "email";
	private  EntityManager  db;
	private  EntityManagerFactory emf;


	ConfigXML c=ConfigXML.getInstance();

     public DataAccess()  {
		if (c.isDatabaseInitialized()) {
			String fileName=c.getDbFilename();

			File fileToDelete= new File(fileName);
			if(fileToDelete.delete()){
				File fileToDeleteTemp= new File(fileName+"$");
				fileToDeleteTemp.delete();

				  System.out.println("File deleted");
				} else {
				  System.out.println("Operation failed");
				}
		}
		open();
		if  (c.isDatabaseInitialized())initializeDB();
		
		System.out.println("DataAccess created => isDatabaseLocal: "+c.isDatabaseLocal()+" isDatabaseInitialized: "+c.isDatabaseInitialized());

		close();

	}
     
    public DataAccess(EntityManager db) {
    	this.db=db;
    }

	
	
	/**
	 * This is the data access method that initializes the database with some events and questions.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try {

		   Calendar today = Calendar.getInstance();
		   
		   int month=today.get(Calendar.MONTH);
		   int year=today.get(Calendar.YEAR);
		   if (month==12) { month=1; year+=1;}  
	    
		   
		    
			
			
			
			db.getTransaction().commit();
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns all the cities where rides depart 
	 * @return collection of cities
	 */
	public List<String> getDepartCities(){
			TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.from FROM Ride r ORDER BY r.from", String.class);
			List<String> cities = query.getResultList();
			return cities;
		
	}
	/**
	 * This method returns all the arrival destinations, from all rides that depart from a given city  
	 * 
	 * @param from the depart location of a ride
	 * @return all the arrival destinations
	 */
	public List<String> getArrivalCities(String from){
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from=?1 ORDER BY r.to",String.class);
		query.setParameter(1, from);
		List<String> arrivingCities = query.getResultList(); 
		return arrivingCities;
		
	}
	/**
	 * This method creates a ride for a driver
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @param nPlaces available seats
	 * @param driverEmail to which ride is added
	 * 
	 * @return the created ride, or null, or an exception
	 * @throws RideMustBeLaterThanTodayException if the ride date is before today 
 	 * @throws RideAlreadyExistException if the same ride already exists for the driver
	 */

    public Ride createRide(RideCreationData rideData, String driverEmail)
            throws RideAlreadyExistException, RideMustBeLaterThanTodayException {

        System.out.println(">> DataAccess: createRide=> from= " + rideData.getFrom() +
                " to= " + rideData.getTo() +
                " driver=" + driverEmail +
                " date " + rideData.getDate());
        ResourceBundle bundle = ResourceBundle.getBundle("Etiquetas");

        try {
            if (new Date().compareTo(rideData.getDate()) > 0) {
                throw new RideMustBeLaterThanTodayException(
                        bundle.getString("CreateRideGUI.ErrorRideMustBeLaterThanToday"));
            }

            db.getTransaction().begin();
            Driver driver = db.find(Driver.class, driverEmail);

            if (driver.doesRideExists(rideData.getFrom(), rideData.getTo(), rideData.getDate())) {
                throw new RideAlreadyExistException(bundle.getString("DataAccess.RideAlreadyExist"));
            }

            Ride ride = driver.addRide(rideData.getFrom(), rideData.getTo(),
                    rideData.getDate(), rideData.getNPlaces(),
                    rideData.getPrice());
            db.persist(driver);
            return ride;

        } catch (NullPointerException e) {
            return null;

        } finally {
            db.getTransaction().commit();
        }
    }

	/**
	 * This method retrieves the rides from two locations on a given date 
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @return collection of rides
	 */
	public List<Ride> getRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getRides=> from= "+from+" to= "+to+" date "+date);

		List<Ride> res = new ArrayList<>();	
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date=?3",Ride.class);   
		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, date);
		List<Ride> rides = query.getResultList();
	 	 for (Ride ride:rides){
		   res.add(ride);
		  }
	 	return res;
	}
	
	/**
	 * This method retrieves from the database the dates a month for which there are events
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride 
	 * @param date of the month for which days with rides want to be retrieved 
	 * @return collection of rides
	 */
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getEventsMonth");
		List<Date> res = new ArrayList<>();	
		
		Date firstDayMonthDate= UtilDate.firstDayMonth(date);
		Date lastDayMonthDate= UtilDate.lastDayMonth(date);
				
		
		TypedQuery<Date> query = db.createQuery("SELECT DISTINCT r.date FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date BETWEEN ?3 and ?4",Date.class);   
		
		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, firstDayMonthDate);
		query.setParameter(4, lastDayMonthDate);
		List<Date> dates = query.getResultList();
	 	 for (Date d:dates){
		   res.add(d);
		  }
	 	return res;
	}
	

public void open(){
		
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);
			  db = emf.createEntityManager();
    	   }
		System.out.println("DataAccess opened => isDatabaseLocal: "+c.isDatabaseLocal());

		
	}

	public void close(){
		// Verificar si hay una transacción activa antes de cerrar
		try {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
		} catch (Exception e) {
			// Ignorar errores de rollback al cerrar
		}
		db.close();
		System.out.println("DataAcess closed");
	}
	public void createUser(String email, String password, boolean driver, String nombre) throws UserAlredyExistException {
		db.getTransaction().begin();
		User user=new User(email,password,driver,nombre);
		
		if (db.find(User.class,email)!=null) {
			db.getTransaction().commit();
			throw new UserAlredyExistException("Ya existe un usuario con ese email");
		}else {
			String tipo = "Driver";
			if(!driver) {
			tipo = "Cliente";
			}else {
				Driver conductor=new Driver(email,nombre);
				db.persist(conductor);
			}
		System.out.println(">> DataAccess: createUser=> email= "+email+" tipo= "+tipo+" nombre= "+ nombre);
		db.persist(user); 
		db.getTransaction().commit();

		}
	
}

public User loguser(String email, String password, boolean driver) throws NonexitstenUserException {
	
	db.getTransaction().begin();

	User puser = db.find(User.class,email);
	if (db.find(User.class,email)==null) {
		db.getTransaction().commit();
		throw new NonexitstenUserException("El usuario no existe");
	}else {
	String tipo = "Driver";
	if(!driver) {
	tipo = "Cliente";
	}
	System.out.println(">> DataAccess: Log in count=> email= "+email+"; tipo: "+tipo);
	User user =db.find(User.class,email);
	db.getTransaction().commit();
	return user;
}

}

public Ride reserva(Ride viaje)throws AnyRidesException{
	db.getTransaction().begin();
	
	if(viaje.getnPlaces()>0) {
		viaje.setBetMinimum((int) (viaje.getnPlaces()-1));
		viaje.setEstado(EstadoViaje.PENDIENTE);
		db.merge(viaje);
		db.getTransaction().commit();
	}else { 
		db.getTransaction().commit();
		throw new AnyRidesException ("No quedan plazas en el viaje");
	}
	return viaje;
	
}

    public List<Ride> getReservedRides(String email) {
        db.getTransaction().begin();
        
        User user = db.find(User.class, email); 
        
        List<Ride> reservedRides = null;
        if (user != null) {
            reservedRides = user.getReservedRides();
        }
        
        db.getTransaction().commit();
        return reservedRides; 
    }

 
    public User addReservedRide(String email, Ride ride) {
        db.getTransaction().begin();

        User user = db.find(User.class, email); 
        if (user != null) {
            user.addReservedRide(ride); 
            db.merge(user); 
        }

        db.getTransaction().commit();
        return user; 
    }
   

    public void addValoracion(Valoracion valoracion) {
        db.getTransaction().begin();
        db.persist(valoracion);
        db.getTransaction().commit();
    }

    
    public List<Valoracion> getValoraciones(String driverEmail) {
        TypedQuery<Valoracion> query = db.createQuery("SELECT v FROM Valoracion v WHERE v.conductor.email = :email", Valoracion.class);
        query.setParameter(EMAIL_PARAMETER, driverEmail);
        return query.getResultList();
    }


    public List<Ride> getReservedRidesF(String email) {
        db.getTransaction().begin();
        User user = db.find(User.class, email);
        List<Ride> rides = user.getReservedRides();
        db.getTransaction().commit();
        return rides;
    }
    
    public List<Ride> getFuturosViajes(String userEmail) {
        List<Ride> futurosViajes = new ArrayList<>();
        Date hoy = new Date(); // Fecha actual

        try {
            TypedQuery<Ride> query = db.createQuery(
                "SELECT r FROM Ride r JOIN r.reservedRides u WHERE u.email = :email AND r.date > :hoy", 
                Ride.class);
            query.setParameter(EMAIL_PARAMETER, userEmail);
            query.setParameter("hoy", hoy);
            
            futurosViajes = query.getResultList();
        } catch (Exception e) {
            System.out.println("Error al obtener futuros viajes: " + e.getMessage());
            e.printStackTrace();
        } finally {
        }

        return futurosViajes;
    }
    
    public List<Valoracion> getValoracionesConductor(String conductorEmail) {
        TypedQuery<Valoracion> query = db.createQuery("SELECT v FROM Valoracion v WHERE v.conductor.email = :email", Valoracion.class);
        query.setParameter(EMAIL_PARAMETER, conductorEmail);
        
        return query.getResultList();
    }
    public List<Ride> getViajesConductor(String conductorEmail) {
        TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.driver.email = :email", Ride.class);
        query.setParameter(EMAIL_PARAMETER, conductorEmail);
        return query.getResultList();
    }

  
  
    
    public Driver findDriverByUserEmail(String userEmail) {
        try {
            TypedQuery<Driver> query = db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class);
            query.setParameter(EMAIL_PARAMETER, userEmail);
            List<Driver> drivers = query.getResultList();
            if (!drivers.isEmpty()) {
                return drivers.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void updateRide(Ride ride) {
        try {
            db.getTransaction().begin();
            db.merge(ride);
            db.getTransaction().commit();
        } catch (Exception e) {
            // Si algo sale mal, hacer rollback
            if (db.getTransaction().isActive()) {
                db.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }
        public List<Ride> getConfirmedRidesByUser(User user) {
            try {
                String jpql = "SELECT r FROM user r WHERE r.user = :user AND r.estado = :estado";
                TypedQuery<Ride> query = db.createQuery(jpql, Ride.class);
                query.setParameter("user", user);
                query.setParameter("estado", EstadoViaje.CONFIRMADO); // EstadoViaje es un Enum
                return query.getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        

    }
        public List<Ride> getReservedRidesByDriver(Driver driver) {
            try {
                // Recuperamos el mismo Driver desde la DB para que pertenezca al mismo EntityManager
                Driver managedDriver = db.find(Driver.class, driver.getName()); 

                if (managedDriver == null) {
                    return new ArrayList<>(); // Si el driver no existe en la DB, devolvemos lista vacía
                }

                String jpql = "SELECT r FROM User u JOIN u.reservedRides r WHERE r.driver = :driver";
                TypedQuery<Ride> query = db.createQuery(jpql, Ride.class);
                query.setParameter("driver", managedDriver);

                return query.getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        
            public HashMap<User, EstadoViaje> getUserRideStatus(Driver driver) {
                HashMap<User, EstadoViaje> userRideStatus = new HashMap<>();

                // Obtener todos los usuarios
                TypedQuery<User> query = db.createQuery("SELECT u FROM User u", User.class);
                List<User> users = query.getResultList();

                // Recorrer cada usuario y verificar sus rides reservados
                for (User user : users) {
                    for (Ride ride : user.getReservedRides()) {
                        if (ride.getDriver().equals(driver)) {
                            userRideStatus.put(user, ride.getEstado());
                        }
                    }
                }
                return userRideStatus;
            }
            
            
            public Monedero getMonedero(String userEmail) throws MonederoNoExisteException, NonexitstenUserException {
                System.out.println(">> DataAccess: getMonedero => userEmail= " + userEmail);
                
                User user = db.find(User.class, userEmail);
                if (user == null) {
                    throw new NonexitstenUserException("El usuario no existe");
                }
                
                Monedero monedero = user.getMonedero();
                if (monedero == null) {
                    throw new MonederoNoExisteException("El usuario no tiene monedero");
                }
                
                return monedero;
            }
            public Monedero ingresarDinero(String userEmail, float cantidad)
                    throws MonederoNoExisteException, NonexitstenUserException, CantidadInvalidaException {
                System.out.println(">> DataAccess: ingresarDinero => userEmail= " + userEmail + ", cantidad= " + cantidad);

                db.getTransaction().begin();


                    User user = db.find(User.class, userEmail);
                    if (user == null) {
                        throw new NonexitstenUserException("El usuario no existe");
                    }

                    Monedero monedero = user.getMonedero();
                    if (monedero == null) {
                        // Si el usuario no tiene monedero, creamos uno
                        monedero = new Monedero(userEmail + "_wallet");
                        monedero.setUser(user);
                        user.setMonedero(monedero);
                    }
                    if(user.getCuenta().getNumeroRandom()<cantidad) {
                        throw new CantidadInvalidaException("No tienes tanto dinero en la cuenta");
                    }

                    monedero.ingresarDinero(cantidad);
                    user.getCuenta().setNumeroRandom((int)(user.getCuenta().getNumeroRandom() - cantidad));
                    db.merge(user);
                    db.getTransaction().commit();

                    return monedero;

            }

            public Monedero retirarDinero(String userEmail, float cantidad)
                    throws MonederoNoExisteException, NonexitstenUserException, CantidadInvalidaException, SaldoInsuficienteException {
                System.out.println(">> DataAccess: retirarDinero => userEmail= " + userEmail + ", cantidad= " + cantidad);

                User user = comprobar_condiciones_entrada(userEmail, cantidad);
                Monedero monedero = user.getMonedero();

                monedero.retirarDinero(cantidad);
                user.getCuenta().setNumeroRandom((int)(user.getCuenta().getNumeroRandom() + cantidad));
                db.merge(user);
                db.getTransaction().commit();

                return monedero;

            }


            private User comprobar_condiciones_entrada(String userEmail, float cantidad)
                    throws MonederoNoExisteException, NonexitstenUserException, CantidadInvalidaException, SaldoInsuficienteException {

                comprobar_cantidad(cantidad);

                db.getTransaction().begin();


                User user = db.find(User.class, userEmail);
                comprobar_user(user);

                Monedero monedero = user.getMonedero();
                comprobar_monedero(monedero, cantidad);

                user.setMonedero(monedero);

                return user;
            }

            private void comprobar_cantidad(float cantidad) throws CantidadInvalidaException {
                if (cantidad <= 0) {
                    throw new CantidadInvalidaException("La cantidad a retirar debe ser mayor que cero");
                }
            }

            private void comprobar_user(User user) throws NonexitstenUserException {
                if (user == null) {
                    throw new NonexitstenUserException("El usuario no existe");
                }
            }

            private void comprobar_monedero(Monedero monedero, float cantidad) throws MonederoNoExisteException, SaldoInsuficienteException {
                if (monedero == null) {
                    throw new MonederoNoExisteException("El usuario no tiene monedero");
                }

                if (!monedero.tieneSaldoSuficiente(cantidad)) {
                    throw new SaldoInsuficienteException("Saldo insuficiente en el monedero");
                }
            }


            public Monedero asociarCuentaBancaria(String userEmail, CuentaBancaria cuentaBancaria)
                    throws MonederoNoExisteException, NonexitstenUserException {
                System.out.println(">> DataAccess: asociarCuentaBancaria => userEmail= " + userEmail);

                db.getTransaction().begin();

                User user = db.find(User.class, userEmail);
                Driver driver= findDriverByUserEmail(userEmail);
                if (user == null) {
                    db.getTransaction().rollback();
                    throw new NonexitstenUserException("El usuario no existe");
                }

                Monedero monedero = user.getMonedero();
                if (monedero == null) {
                    // Si el usuario no tiene monedero, creamos uno
                    monedero = new Monedero(userEmail + "_wallet");
                    monedero.setUser(user);
                    monedero.setD(driver);
                    user.setMonedero(monedero);
                    driver.setMonedero(monedero);
                }

                monedero.setCuentaBancaria(cuentaBancaria);
                user.setCuenta(cuentaBancaria);
                driver.setCuenta(cuentaBancaria);
                db.merge(user);
                db.merge(driver);
                db.getTransaction().commit();

                return monedero;
            }

            public float consultarSaldo(String userEmail) 
                    throws MonederoNoExisteException, NonexitstenUserException {
                System.out.println(">> DataAccess: consultarSaldo => userEmail= " + userEmail);
                
                User user = db.find(User.class, userEmail);
                if (user == null) {
                    throw new NonexitstenUserException("El usuario no existe");
                }
                
                Monedero monedero = user.getMonedero();
                if (monedero == null) {
                    throw new MonederoNoExisteException("El usuario no tiene monedero");
                }
                
                return monedero.getSaldo();
            }
        public void updatearUser(User usuario) {
        	 try {
                 db.getTransaction().begin();
                 db.merge(usuario);
                 db.getTransaction().commit();
             } catch (Exception e) {
                 // Si algo sale mal, hacer rollback
                 if (db.getTransaction().isActive()) {
                     db.getTransaction().rollback();
                 }
                 e.printStackTrace();
             }
        }
         
        	 public void updatearDriver(Driver d) {
            	 try {
                     db.getTransaction().begin();
                     db.merge(d);
                     db.getTransaction().commit();
                 } catch (Exception e) {
                     // Si algo sale mal, hacer rollback
                     if (db.getTransaction().isActive()) {
                         db.getTransaction().rollback();
                     }
                     e.printStackTrace();
        }
        	 }
        	 
        	 public void cobro(Monedero mon, float cantidad) {
        		 db.getTransaction().begin();
        		 mon.setSaldo(mon.getSaldo()-cantidad);
        		 if(mon.getSaldo()<0) {
        			 //Error
        		 }
        		 else {
        			 db.merge(mon);
        			 db.getTransaction().commit();
        			 
        		 }
        	 }



        }




