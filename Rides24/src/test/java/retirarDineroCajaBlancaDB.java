import dataAccess.DataAccess;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.NonexitstenUserException;
import exceptions.SaldoInsuficienteException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class retirarDineroCajaBlancaDB {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void test1() { //Cantidad inválida
        String userEmail = "userf@falso.com";
        float cantidad = -2;

        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            assertTrue("Cantidad negativa no válida", true);
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test2() { //Usuario no existe
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test3() { //Monedero no existe
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 30);
            testDA.close();

            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar MonederoNoExisteException");

        } catch (MonederoNoExisteException e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            assertTrue("Fondos insuficientes en cuenta", true);
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void test4() { //Saldo insuficiente
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 30);
            testDA.close();

            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar SaldoInsuficienteException");

        } catch (SaldoInsuficienteException e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNotNull("El monedero debería haberse creado", userAfterTest.getMonedero());
            testDA.close();

        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void test5() { //Retiro exitoso
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debería haberse creado", result);
            assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.01);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe coincidir", cantidad, userAfterTest.getMonedero().getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse reducido", 50.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01);
            testDA.close();

        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { /* ignore */ }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }
}