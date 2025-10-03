import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.Driver;
import domain.Monedero;
import domain.User;
import exceptions.NonexitstenUserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.*;

import static org.junit.Assert.*;

public class asociasCuentaBancariaCajaBlanca {
    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test
    public void asociarCuentaBancariaTest1_UsuarioNoExiste() {
        String Useremail="userfalso@falso.com";
        CuentaBancaria cuentaBancaria= new CuentaBancaria("1234567890");
        Mockito.when(db.find(User.class,Useremail)).thenReturn(null);

        try{
            sut.open();
            sut.asociarCuentaBancaria(Useremail,cuentaBancaria);
            sut.close();
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        }

    }

    @Test
    public void asociarCuentaBancariaTest2_MonederoNoExiste() {
        String Useremail="userfalso@falso.com";
        CuentaBancaria cuentaBancaria= new CuentaBancaria("1234567890");
        User usuarioFalso = new User(Useremail, "userfalso", false, "UserF");
        Monedero monederoExistente = new Monedero(Useremail + "_wallet");
        usuarioFalso.setMonedero(monederoExistente);
        Driver falsoDriver=new Driver(Useremail,"UserF");
        falsoDriver.setMonedero(monederoExistente);
        Mockito.when(db.find(User.class,Useremail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));

        try{
            sut.open();
            sut.asociarCuentaBancaria(Useremail,cuentaBancaria);
            sut.close();
            assertNotNull(usuarioFalso.getCuenta());//User tiene cuenta
            assertNotNull(usuarioFalso.getMonedero());//User tiene monedero
            assertNotNull(falsoDriver.getCuenta());//Driver tiene monedero
            assertNotNull(falsoDriver.getMonedero());//Driver tiene monedero
            assertNotNull(falsoDriver);
            assertNotNull(usuarioFalso);
            assertEquals(cuentaBancaria,usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getCuenta(),usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getMonedero(),falsoDriver.getMonedero());

        } catch (NonexitstenUserException e) {
            fail("No deberia saltar la excepcion");
        }

        }


    @Test
    public void asociarCuentaBancariaTest3_MonederoNoExiste() {
        String Useremail="userfalso@falso.com";
        CuentaBancaria cuentaBancaria= new CuentaBancaria("1234567890");
        User usuarioFalso = new User(Useremail, "userfalso", false, "UserF");
        Driver falsoDriver=new Driver(Useremail,"UserF");
        Mockito.when(db.find(User.class,Useremail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));

        try{
            sut.open();
            sut.asociarCuentaBancaria(Useremail,cuentaBancaria);
            sut.close();
            assertNotNull(usuarioFalso.getCuenta());//User tiene cuenta
            assertNotNull(usuarioFalso.getMonedero());//User tiene monedero
            assertNotNull(falsoDriver.getCuenta());//Driver tiene monedero
            assertNotNull(falsoDriver.getMonedero());//Driver tiene monedero
            assertNotNull(falsoDriver);
            assertNotNull(usuarioFalso);
            assertEquals(cuentaBancaria,usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getCuenta(),usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getMonedero(),falsoDriver.getMonedero());

        } catch (NonexitstenUserException e) {
            fail("No deberia saltar la excepcion");
        }


    }


}
