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

public class retirarDineroCajaNegra {
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
        int cantidad = 100; //Salgo que queremos ingresar en el monedero
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monederoFalso = new Monedero();
        usuarioFalso.setMonedero(monederoFalso);
        monederoFalso.setSaldo(120.0f);
        int saldoesperadocuenta=usuarioFalso.getCuenta().getNumeroRandom()+cantidad;
        int nuevosaldo= (int) (monederoFalso.getSaldo()-cantidad);
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        try{
            sut.open();
            sut.retirarDinero(UserEmail,cantidad);
            sut.close();
            assertEquals(nuevosaldo, usuarioFalso.getMonedero().getSaldo(), 0.01f);
            assertEquals(saldoesperadocuenta, usuarioFalso.getCuenta().getNumeroRandom(), 0.01f);
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
        } catch (SaldoInsuficienteException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCajaNegra2_CantidadNegativa() {
        String UserEmail = "usuariofalso@falso.com";
        float cantidad = -5; // Cantidad negativa
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monederoFalso = new Monedero();
        usuarioFalso.setMonedero(monederoFalso);
        monederoFalso.setSaldo(120.0f);
        
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        
        try{
            sut.open();
            sut.retirarDinero(UserEmail, cantidad);
            sut.close();
            fail("Debería lanzar CantidadInvalidaException por cantidad negativa");
        } catch (CantidadInvalidaException e) {
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
            assertTrue("Cantidad negativa detectada correctamente", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
    
    @Test
    public void testCajaNegra8_CantidadCero() {
        String UserEmail = "usuariofalso@falso.com";
        float cantidad = 0; // Cantidad cero
        User usuarioFalso = new User(UserEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monederoFalso = new Monedero();
        usuarioFalso.setMonedero(monederoFalso);
        monederoFalso.setSaldo(120.0f);
        
        Mockito.when(db.find(User.class, UserEmail)).thenReturn(usuarioFalso);
        
        try{
            sut.open();
            sut.retirarDinero(UserEmail, cantidad);
            sut.close();
            fail("Debería lanzar CantidadInvalidaException por cantidad cero");
        } catch (CantidadInvalidaException e) {
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
            assertTrue("Cantidad cero detectada correctamente", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra3() {
        // Test case 3: userEmail es null
        String userEmail = null;
        int cantidad = 50;
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4() {
        // Test case 4: Usuario no existe en la DB
        String userEmail = "usuarionoexiste@falso.com";
        int cantidad = 50;
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
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
        // Test case 5: Usuario tiene cuenta pero no monedero
        String userEmail = "usuariofalso@falso.com";
        int cantidad = 50;
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        usuarioFalso.setMonedero(null); // Sin monedero
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();
            fail("Debería lanzar MonederoNoExisteException");
        } catch (MonederoNoExisteException e) {
            assertEquals("El usuario no tiene monedero", e.getMessage());
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra6() {
        // Test case 6: Saldo insuficiente en monedero
        String userEmail = "usuariofalso@falso.com";
        int cantidad = 100;
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monederoFalso = new Monedero();
        monederoFalso.setSaldo(50.0f); // Solo 50€ en el monedero
        usuarioFalso.setMonedero(monederoFalso);
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();
            fail("Debería lanzar SaldoInsuficienteException");
        } catch (SaldoInsuficienteException e) {
            assertEquals("Saldo insuficiente en el monedero", e.getMessage());
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra7() {
        // Test case 7: Caso exitoso - Usuario con monedero y saldo suficiente
        String userEmail = "usuariofalso@falso.com";
        int cantidad = 30;
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monederoFalso = new Monedero();
        monederoFalso.setSaldo(50.0f); // 50€ en el monedero
        usuarioFalso.setMonedero(monederoFalso);
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();
            assertEquals(20.0f, usuarioFalso.getMonedero().getSaldo(), 0.01f);
            assertEquals(130, usuarioFalso.getCuenta().getNumeroRandom(), 0.01f);
            assertNotNull(usuarioFalso.getMonedero());
            assertNotNull(usuarioFalso.getCuenta());
            assertTrue("Funciona correctamente", true);
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }
}
