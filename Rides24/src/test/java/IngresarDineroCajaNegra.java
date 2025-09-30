import dataAccess.DataAccess;
import domain.*;
import exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IngresarDineroCajaNegra {
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
        int cantidad = 50; //Salgo que queremos ingresar en el monedero
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monedero = new Monedero();
        usuarioFalso.setMonedero(monedero);
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        try {
            sut.open();
            sut.ingresarDinero(UserEmail, cantidad);
            sut.close();
            assertEquals(50.0f, usuarioFalso.getMonedero().getSaldo(), 0.01f);
            assertEquals(50.0f, usuarioFalso.getCuenta().getNumeroRandom(), 0.01f);
            assertNotNull(usuarioFalso.getMonedero());
            assertNotNull(usuarioFalso.getCuenta());
            assertNotNull(usuarioFalso);
            assertTrue("Funciona correctamente", true);
        } catch (NonexitstenUserException e) {
            fail("No deberia saltar esta excepcion");
            throw new RuntimeException(e);
        } catch (MonederoNoExisteException e) {
            fail("No deberia saltar esta excepcion");
            throw new RuntimeException(e);
        } catch (CantidadInvalidaException e) {
            fail("No deberia saltar esta excepcion");
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testCajaNegra2_SinMonederoExistente() {
        String UserEmail = "usuariofalso@falso.com";
        float cantidad = 50;
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        usuarioFalso.setMonedero(null); // Sin monedero inicial
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        
        try {
            sut.open();
            Monedero result = sut.ingresarDinero(UserEmail, cantidad);
            sut.close();
            
            assertNotNull("Monedero creado automáticamente", usuarioFalso.getMonedero());
            assertEquals(50.0f, result.getSaldo(), 0.01f);
            assertEquals(50, usuarioFalso.getCuenta().getNumeroRandom());
            assertTrue("Funciona correctamente", true);
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra3_CantidadNegativa() {
        String UserEmail = "usuariofalso@falso.com";
        float cantidad = -5; // Cantidad negativa
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        
        try {
            sut.open();
            sut.ingresarDinero(UserEmail, cantidad);
            sut.close();
            fail("Debería lanzar CantidadInvalidaException por cantidad negativa");
        } catch (CantidadInvalidaException e) {
            assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());
            assertTrue("Cantidad negativa detectada correctamente", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4() {
        String UserEmail = null;
        int cantidad = 50;
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(null);
        try {
            sut.open();
            sut.ingresarDinero(UserEmail, cantidad);
            sut.close();
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
    @Test
    public void testCajaNegra5(){
        String userEmail = "usuarionoexiste@falso.com";
        int cantidad = 50;
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        try{
            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
            sut.close();
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
    @Test
    public void testCajaNegra6_SaldoInsuficiente(){
        String UserEmail = "usuariofalso@falso.com";
        float cantidad = 120; // Más dinero del que tiene en cuenta
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100); // Solo tiene 100
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        
        try{
            sut.open();
            sut.ingresarDinero(UserEmail, cantidad);
            sut.close();
            fail("Debería lanzar CantidadInvalidaException por saldo insuficiente");
        } catch (CantidadInvalidaException e) {
            assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());
            assertTrue("Saldo insuficiente detectado correctamente", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
}

