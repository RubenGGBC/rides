package businessLogic;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import domain.Booking;
import domain.Ride;
import domain.CuentaBancaria;
import domain.Driver;
import domain.Monedero;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.SaldoInsuficienteException;
import exceptions.RideAlreadyExistException;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.UserAlredyExistException;
import exceptions.AnyRidesException;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.NonexitstenUserException;
import domain.User;
import domain.Valoracion;

import javax.jws.WebMethod;
import javax.jws.WebService;
 
/**
 * Interface that specifies the business .
 */
@WebService
public interface BLFacade  {   
	  
	/**
	 * This method returns all the cities where rides depart  
	 * @return collection of cities
	 */
	@WebMethod public List<String> getDepartCities();
	 
	/**
	 * This method returns all the arrival destinations, from all rides that depart from a given city  
	 * 
	 * @param from the depart location of a ride
	 * @return all the arrival destinations
	 */
	@WebMethod public List<String> getDestinationCities(String from);


	/**
	 * This method creates a ride for a driver
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @param nPlaces available seats
	 * @param driver to which ride is added
	 * 
	 * @return the created ride, or null, or an exception
	 * @throws RideMustBeLaterThanTodayException if the ride date is before today 
 	 * @throws RideAlreadyExistException if the same ride already exists for the driver
	 */
   @WebMethod
   public Ride createRide( String from, String to, Date date, int nPlaces, float price, String driverEmail) throws RideMustBeLaterThanTodayException, RideAlreadyExistException;
	
	
	/**
	 * This method retrieves the rides from two locations on a given date 
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @return collection of rides
	 */
	@WebMethod public List<Ride> getRides(String from, String to, Date date);
	
	/**
	 * This method retrieves from the database the dates a month for which there are events
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride 
	 * @param date of the month for which days with rides want to be retrieved 
	 * @return collection of rides
	 */
	@WebMethod public List<Date> getThisMonthDatesWithRides(String from, String to, Date date);
	
	/**
	 * This method calls the data access to initialize the database with some events and questions.
	 * It is invoked only when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	@WebMethod public void initializeBD();

	public void createUser(String email,String password,boolean driver, String nombre)throws UserAlredyExistException;
	
	 public User logUser(String email, String password, boolean driver) throws NonexitstenUserException;
	
	 public Ride reserva(Ride viaje)throws AnyRidesException;
	 
	 public void addRide(Ride viaje, String email)throws AnyRidesException;
	 
	 public List<Ride> getRidesByDriver(Driver conductor);
	 
	 public void addValoracion(Valoracion valoracion);
	 
	 public List<Valoracion> getValoraciones(String driverEmail);
	 
	 public List<Ride> getReservedRides(String email);
	 
	 public Driver getDriverByUser(User user);
	 
	 public void updateRide(Ride ride);
	 public List<Ride> getReservedRidesByDriver(Driver driver);
	 
	 public Monedero getMonedero(String userEmail) throws MonederoNoExisteException, NonexitstenUserException;
	 
	 public Monedero ingresarDinero(String userEmail, float cantidad) 
	            throws MonederoNoExisteException, NonexitstenUserException, CantidadInvalidaException;
	 public Monedero retirarDinero(String userEmail, float cantidad) 
	            throws MonederoNoExisteException, NonexitstenUserException, CantidadInvalidaException, SaldoInsuficienteException;
	 public Monedero asociarCuentaBancaria(String userEmail, CuentaBancaria cuentaBancaria) 
	            throws MonederoNoExisteException, NonexitstenUserException;
	 public float consultarSaldo(String userEmail) 
	            throws MonederoNoExisteException, NonexitstenUserException;
	 public void updatearUser(User usuario);
	 
	 public void updatearDriver(Driver d);
	    
   	 public void cobro(Monedero mon, float cantidad);
}










