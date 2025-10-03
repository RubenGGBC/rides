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

public class asociarCuentaBancariaCajaNegra {
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
    public void testCajaNegra1() {
        String UserEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        Driver falsoDriver = new Driver(UserEmail, "UserF");
        
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));
        
        try{
            sut.open();
            sut.asociarCuentaBancaria(UserEmail, cuentaBancaria);
            sut.close();
            assertNotNull(usuarioFalso.getCuenta());
            assertNotNull(usuarioFalso.getMonedero());
            assertNotNull(falsoDriver.getCuenta());
            assertNotNull(falsoDriver.getMonedero());
            assertEquals(cuentaBancaria, usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getCuenta(), usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getMonedero(), usuarioFalso.getMonedero());
            assertTrue("Funciona correctamente", true);
        } catch (NonexitstenUserException e) {
            fail("No debería saltar esta excepción");
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCajaNegra2_EmailNull() {
        String userEmail = null;
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        
        try{
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
    
    @Test
    public void testCajaNegra3_CuentaBancariaNull() {
        String userEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = null;
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        Driver falsoDriver = new Driver(userEmail, "UserF");
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));
        
        try{
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            assertNull(usuarioFalso.getCuenta());
            assertNotNull(usuarioFalso.getMonedero());
            assertNull(falsoDriver.getCuenta());
            assertNotNull(falsoDriver.getMonedero());
            assertEquals(falsoDriver.getMonedero(), usuarioFalso.getMonedero());
        } catch (NonexitstenUserException e) {
            fail("No debería saltar esta excepción");
        }
    }

    @Test
    public void testCajaNegra4() {
        String userEmail = "usuarionoexiste@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra5() {
        String userEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        Monedero monederoExistente = new Monedero(userEmail + "_wallet");
        monederoExistente.setSaldo(50.0f);
        usuarioFalso.setMonedero(monederoExistente);
        Driver falsoDriver = new Driver(userEmail, "UserF");
        falsoDriver.setMonedero(monederoExistente);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));
        
        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            assertNotNull(usuarioFalso.getCuenta());
            assertNotNull(usuarioFalso.getMonedero());
            assertNotNull(falsoDriver.getCuenta());
            assertNotNull(falsoDriver.getMonedero());
            assertEquals(cuentaBancaria, usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getCuenta(), usuarioFalso.getCuenta());
            assertEquals(falsoDriver.getMonedero(), usuarioFalso.getMonedero());
            assertEquals(50.0f, usuarioFalso.getMonedero().getSaldo(), 0.01f);
            assertTrue("Funciona correctamente", true);
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra6() {
        String userEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Collections.emptyList());
        
        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            assertNotNull(usuarioFalso.getCuenta());
            assertNotNull(usuarioFalso.getMonedero());
            assertEquals(cuentaBancaria, usuarioFalso.getCuenta());
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }
}