package businessLogic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebService;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Ride;
import domain.CuentaBancaria;
import domain.Driver;
import domain.Monedero;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.SaldoInsuficienteException;
import exceptions.AnyRidesException;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.NonexitstenUserException;
import domain.User;
import domain.Valoracion;
import exceptions.UserAlredyExistException;
import exceptions.RideAlreadyExistException;

/**
 * It implements the business logic as a web service.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation  implements BLFacade {
	private static final Logger logger = Logger.getLogger(BLFacadeImplementation.class.getName());
	private User loggedUser;

	public void setLoggedUser(User user) {
	    this.loggedUser = user;
	}

	public User getLoggedUser() {
	    return loggedUser;
	}
	DataAccess dbManager;

	public BLFacadeImplementation()  {		
		logger.info("Creating BLFacadeImplementation instance");
		
		
		    dbManager=new DataAccess();
		
	}
	
    public BLFacadeImplementation(DataAccess da)  {
		
		logger.info("Creating BLFacadeImplementation instance with DataAccess parameter");
		ConfigXML c=ConfigXML.getInstance();
		
		dbManager=da;		
	}
    
    
    /**
     * {@inheritDoc}
     */
    @WebMethod public List<String> getDepartCities(){
    	dbManager.open();	
		
		 List<String> departLocations=dbManager.getDepartCities();		

		dbManager.close();
		
		return departLocations;
    	
    }
    /**
     * {@inheritDoc}
     */
	@WebMethod public List<String> getDestinationCities(String from){
		dbManager.open();	
		
		 List<String> targetCities=dbManager.getArrivalCities(from);		

		dbManager.close();
		
		return targetCities;
	}

	/**
	 * {@inheritDoc}
	 */
   @WebMethod
   public Ride createRide( String from, String to, Date date, int nPlaces, float price, String driverEmail ) throws RideMustBeLaterThanTodayException, RideAlreadyExistException{
	   
		dbManager.open();
		Ride ride=dbManager.createRide(from, to, date, nPlaces, price, driverEmail);		
		dbManager.close();
		return ride;
   }
	
   /**
    * {@inheritDoc}
    */
	@WebMethod 
	public List<Ride> getRides(String from, String to, Date date){
		dbManager.open();
		List<Ride>  rides=dbManager.getRides(from, to, date);
		dbManager.close();
		return rides;
	}

    
	/**
	 * {@inheritDoc}
	 */
	@WebMethod 
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date){
		dbManager.open();
		List<Date>  dates=dbManager.getThisMonthDatesWithRides(from, to, date);
		dbManager.close();
		return dates;
	}
	
	
	public void close() {
		DataAccess dB4oManager=new DataAccess();

		dB4oManager.close();

	}

	/**
	 * {@inheritDoc}
	 */
    @WebMethod	
	 public void initializeBD(){
    	dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}

	
	 
	    public void createUser(String email, String password, boolean driver, String nombre) throws UserAlredyExistException{
	 	   
	 		dbManager.open();
	 		dbManager.createUser(email, password, nombre);
	 		dbManager.close();
	 	
	    }
	    
	     
	    public User logUser(String email, String password, boolean driver) throws NonexitstenUserException{
	    	dbManager.open();
	    	User user=dbManager.loguser(email, password, driver);
	    	dbManager.close();
	    	return user;
	    }
	    
	    public Ride reserva(Ride viaje) throws AnyRidesException {
	        dbManager.open();
	        Ride reserva = dbManager.reserva(viaje);

	        dbManager.close();
	        return reserva;
	    }
	    
	
	 public void addRide(Ride viaje, String email)throws AnyRidesException{
		 dbManager.open();
		 dbManager.addReservedRide(email, viaje);
		 dbManager.close();
	 }
	 
	 

	 
	 
	 public List<Ride> getFuturosViajes(String userEmail) {
	     dbManager.open();
	     List<Ride> viajes = dbManager.getFuturosViajes(userEmail);
	     dbManager.close();
	     return viajes;
	 }
	    
	     
	    public void addValoracion(Valoracion valoracion) {
	        dbManager.open();
	        dbManager.addValoracion(valoracion);
	        dbManager.close();
	    }
	    
	    
	    public List<Valoracion> getValoraciones(String driverEmail) {
	        dbManager.open();
	        List<Valoracion> valoraciones = dbManager.getValoraciones(driverEmail);
	        dbManager.close();
	        return valoraciones;
	    }

		@Override
		 public List<Ride> getReservedRides(String email)	{
			
		 dbManager.open();
		 
		List<Ride> l= dbManager.getReservedRides(email);
		dbManager.close();
			// TODO Auto-generated method stub
		return l;
		}
		public Driver getDriverByUser(User user) {
			dbManager.open();
		    if (user == null) return null;
		    return dbManager.findDriverByUserEmail(user.getEmail());
		}

		@Override
		public List<Ride> getRidesByDriver(Driver conductor) {
			// TODO Auto-generated method stub
			return null;
		}
		public void updateRide(Ride ride) {
			dbManager.open();
		    dbManager.updateRide(ride);
		    dbManager.close();
		}
		public List<Ride> getConfirmedRidesByUser(User user) {
			dbManager.open();
		    return dbManager.getConfirmedRidesByUser(user);
		    
		}
		
		public List<Ride> getReservedRidesByDriver(Driver driver) {
		  dbManager.open();
		  return dbManager.getReservedRidesByDriver(driver);
		    }
		
		public Monedero getMonedero(String userEmail) throws MonederoNoExisteException, NonexitstenUserException {
		    dbManager.open();
		    Monedero monedero = dbManager.getMonedero(userEmail);
		    dbManager.close();
		    return monedero;
		
		}
		public Monedero ingresarDinero(String userEmail, float cantidad) 
		        throws MonederoNoExisteException, NonexitstenUserException, CantidadInvalidaException {
		    if (cantidad <= 0) {
		        throw new CantidadInvalidaException("La cantidad a ingresar debe ser mayor que cero");
		    }
		    
		    dbManager.open();
		    Monedero monedero = dbManager.ingresarDinero(userEmail, cantidad);
		    dbManager.close();
		    return monedero;
		}
		public Monedero retirarDinero(String userEmail, float cantidad) 
		        throws MonederoNoExisteException, NonexitstenUserException, CantidadInvalidaException, SaldoInsuficienteException {
		    if (cantidad <= 0) {
		        throw new CantidadInvalidaException("La cantidad a retirar debe ser mayor que cero");
		    }
		    
		    dbManager.open();
		    Monedero monedero = dbManager.retirarDinero(userEmail, cantidad);
		    dbManager.close();
		    return monedero;
		}
		public Monedero asociarCuentaBancaria(String userEmail, CuentaBancaria cuentaBancaria) 
		        throws MonederoNoExisteException, NonexitstenUserException {
		    dbManager.open();
		    Monedero monedero = dbManager.asociarCuentaBancaria(userEmail, cuentaBancaria);
		    dbManager.close();
		    return monedero;
		}
		public float consultarSaldo(String userEmail) 
		        throws MonederoNoExisteException, NonexitstenUserException {
		    dbManager.open();
		    float saldo = dbManager.consultarSaldo(userEmail);
		    dbManager.close();
		    return saldo;
		}
		public void updatearUser(User usuario) {
			dbManager.open();
			dbManager.updatearUser(usuario);
			dbManager.close();
		}
   	 public void updatearDriver(Driver d) {
   		dbManager.open();
		dbManager.updatearDriver(d);
		dbManager.close();
   	 }
	 public void cobro(Monedero mon, float cantidad) {
		 dbManager.open();
		 dbManager.cobro(mon, cantidad);
		 dbManager.close();
	 }


		
}


		


	



	
	




