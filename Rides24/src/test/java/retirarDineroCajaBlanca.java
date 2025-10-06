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

public class retirarDineroCajaBlanca {
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
    public void test1() { // Cantidad inválida
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = -2;

        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test2() { //Usuario no existe
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = 100.0f;

        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);

        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test3() { // Usuario sin monedero
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 100.0f;

        User usuarioFalso = new User(userEmail, pass, false, userName);
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(30);
        usuarioFalso.setMonedero(null);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);

        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar MonederoNoExisteException");

        } catch (MonederoNoExisteException e) {
            assertTrue("El usuario no tiene monedero", true);
        } catch (SaldoInsuficienteException e) {
            throw new RuntimeException(e);
        } catch (NonexitstenUserException e) {
            throw new RuntimeException(e);
        } catch (CantidadInvalidaException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void test4() { //Saldo insuficiente
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 100.0f;

        User usuarioFalso = new User(userEmail, pass, false, userName);
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(30);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(20.0f);
        usuarioFalso.setMonedero(monedero);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);

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

    @Test
    public void test5() {
        // Test case 5: Caso exitoso - retiro de dinero
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 100.0f;

        User usuarioFalso = new User(userEmail, pass, false, userName);
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(100.0f); // Saldo exacto
        usuarioFalso.setMonedero(monedero);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);

        try {
            Monedero result = sut.retirarDinero(userEmail, cantidad);

            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe coincidir", 0.0f, result.getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse incrementado", 200.0f, usuarioFalso.getCuenta().getNumeroRandom(), 0.00f);

        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

}