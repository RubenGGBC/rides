import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.NonexitstenUserException;
import exceptions.SaldoInsuficienteException;
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
    public void testCajaNegra1() { // Caso exitoso - Usuario con monedero y saldo suficiente (retira 100€ de 120€)
        String userEmail = "rgallego007@ikasle.ehu.eus";
        int cantidad = 100;

        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monederoFalso = new Monedero(userEmail + "_wallet");
        monederoFalso.setSaldo(120.0f);
        usuarioFalso.setMonedero(monederoFalso);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);

        try {
            Monedero result = sut.retirarDinero(userEmail, cantidad);

            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe haberse reducido", 20.0f, result.getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse incrementado", 200.0f, usuarioFalso.getCuenta().getNumeroRandom(), 0.01f);

        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra2() {// Cantidad inválida (negativa)
        String userEmail = "rgallego007@ikasle.ehu.eus";
        int cantidad = -5;

        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monederoFalso = new Monedero(userEmail + "_wallet");
        monederoFalso.setSaldo(120.0f);
        usuarioFalso.setMonedero(monederoFalso);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);

        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra3() { // userEmail es null
        String userEmail = null;
        int cantidad = 50;

        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);

        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (IllegalArgumentException e) {
            assertTrue("Email null genera IllegalArgumentException", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4() { //Usuario no existe en la DB
        String userEmail = "usuarionoexiste@falso.com";
        int cantidad = 50;

        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);

        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra5() {// Usuario sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        int cantidad = 50;

        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        usuarioFalso.setMonedero(null);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);

        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar MonederoNoExisteException");

        } catch (MonederoNoExisteException e) {
            assertEquals("El usuario no tiene monedero", e.getMessage());
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra6() { //Saldo insuficiente
        String userEmail = "rgallego007@ikasle.ehu.eus";
        int cantidad = 50;

        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(30);

        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(20.0f);
        usuarioFalso.setMonedero(monedero);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso)


        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar SaldoInsuficienteException");

        } catch (SaldoInsuficienteException e) {
            assertEquals("Saldo insuficiente en el monedero", e.getMessage());
            assertNotNull("El usuario debería existir", usuarioFalso);
            assertNotNull("El monedero debería existir", usuarioFalso.getMonedero());
        } catch (Exception e) {
            fail("Excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
}
