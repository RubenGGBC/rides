import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.NonexitstenUserException;
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
        // Test case 1: Usuario existe, tiene cuenta con suficiente dinero, tiene monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50.0f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setUser(usuarioFalso);
        usuarioFalso.setMonedero(monedero);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            
            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse reducido", 50.0f, usuarioFalso.getCuenta().getNumeroRandom(), 0.01f);
            
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra2() {
        // Test case 2: Usuario existe, tiene cuenta con suficiente dinero, sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50.0f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        // Sin monedero inicial
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            
            assertNotNull("El monedero debería haberse creado", result);
            assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.01);
            assertNotNull("El monedero debería existir", usuarioFalso.getMonedero());
            assertEquals("El saldo de cuenta debe haberse reducido", 50.0f, usuarioFalso.getCuenta().getNumeroRandom(), 0.01f);
            
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra3() {
        // Test case 3: Cantidad negativa
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = -5.0f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            sut.ingresarDinero(userEmail, cantidad);
            fail("Debería lanzar CantidadInvalidaException");
            
        } catch (CantidadInvalidaException e) {
            fail("ERROR: El test debería fallar - Se esperaba que NO se lance CantidadInvalidaException pero sí se lanzó: " + e.getMessage());
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4() {
        // Test case 4: userEmail es null
        String userEmail = null;
        float cantidad = 50.0f;
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        
        try {
            sut.ingresarDinero(userEmail, cantidad);
            fail("Debería lanzar excepción");
            
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (IllegalArgumentException e) {
            assertTrue("Email null genera IllegalArgumentException", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra5() {
        // Test case 5: Usuario no existe en la DB
        String userEmail = "usuarionoexiste@falso.com";
        float cantidad = 50.0f;
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        
        try {
            sut.ingresarDinero(userEmail, cantidad);
            fail("Debería lanzar NonexitstenUserException");
            
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra6() {
        // Test case 6: Usuario con saldo insuficiente en cuenta
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 120.0f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100); // Solo tiene 100
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            sut.ingresarDinero(userEmail, cantidad);
            fail("Debería lanzar CantidadInvalidaException");
            
        } catch (CantidadInvalidaException e) {
            assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());
            assertNotNull("El usuario debería existir", usuarioFalso);
            assertNotNull("El monedero debería haberse creado", usuarioFalso.getMonedero());
            
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra7_ValorLimiteMinimo() {
        // Test case 7: Valor límite mínimo - cantidad = 0.01 con monedero existente
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 0.01f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(50.0f);
        monedero.setUser(usuarioFalso);
        usuarioFalso.setMonedero(monedero);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            
            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe coincidir", 50.01f, result.getSaldo(), 0.001);
            assertEquals("El saldo de cuenta debe haberse reducido", 99.99f, usuarioFalso.getCuenta().getNumeroRandom(), 1.0f);
            
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra8_ValorLimiteMaximo() {
        // Test case 8: Valor límite máximo - cantidad = 99.99 sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 99.99f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        // Sin monedero inicial
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            
            assertNotNull("El monedero debería haberse creado", result);
            assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.001);
            assertNotNull("El monedero debería existir", usuarioFalso.getMonedero());
            assertEquals("El saldo de cuenta debe haberse reducido", 0.01f, usuarioFalso.getCuenta().getNumeroRandom(), 0.02f);
            
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }
}

