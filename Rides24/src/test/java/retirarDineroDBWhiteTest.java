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

public class retirarDineroDBWhiteTest {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void test1() { //Cantidad inválida
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = -5;

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
        String userEmail = "mberasategui022@ikasle.ehu.eus";
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
    public void test3() {
        // Cantidad>0, User está en la DB, pero no tiene monedero
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = 100.0f;

        try {
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            // Crear usuario SIN monedero
            testDA.open();
            testDA.crearUserSinMonedero(userEmail, "password123", "TestUser", 500);
            testDA.close();

            // Intentar retirar dinero
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar MonederoNoExisteException");

        } catch (MonederoNoExisteException e) {
            try { sut.close(); } catch (Exception ex) {  }
            assertTrue("El usuario no tiene monedero", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
            }
        } finally {
            try {
                testDA.open();
                testDA.removeUser(userEmail);
                testDA.close();
            } catch (Exception ex) {  }
        }
    }

    @Test
    public void test4() {
        // Cantidad>0, User en DB con monedero, saldo insuficiente
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = 100.0f;

        try {
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            // Crear usuario con monedero con saldo menor que cantidad
            testDA.open();
            testDA.crearUserConMonederoSaldoInsuficiente(userEmail, "password123", "TestUser", 500, 50.0f);
            testDA.close();

            // Intentar retirar más dinero del que tiene
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar SaldoInsuficienteException");

        } catch (SaldoInsuficienteException e) {
            try { sut.close(); } catch (Exception ex) {  }
            assertTrue("Saldo insuficiente en el monedero", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {
                fail("Excepción inesperada: " + e.getClass().getSimpleName());
            }
        } finally {
            // Limpiar base de datos
            try {
                testDA.open();
                testDA.removeUser(userEmail);
                testDA.close();
            } catch (Exception ex) {  }
        }
    }

    @Test
    public void test5() {
        // Cantidad>0, User en DB con monedero, saldo suficiente
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = 100.0f;
        float saldoInicial = 200.0f;
        int cuentaInicial = 500;

        try {
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            // Crear usuario con monedero con saldo suficiente
            testDA.open();
            testDA.crearUserConMonederoSaldoSuficiente(userEmail, "password123", "TestUser", cuentaInicial, saldoInicial);
            testDA.close();

            sut.open();
            Monedero monederoRetornado = sut.retirarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debe existir", monederoRetornado);
            float saldoEsperado = saldoInicial - cantidad;
            assertEquals("El saldo debe ser = saldoInicial - cantidad", saldoEsperado, monederoRetornado.getSaldo(), 0.01);

            testDA.open();
            User userVerificacion = testDA.getUser(userEmail);
            assertEquals("El saldo del monedero en DB debe estar actualizado",
                    saldoEsperado, userVerificacion.getMonedero().getSaldo(), 0.01);
            testDA.close();


        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { }
            if (e.getClass().getSimpleName().contains("RollbackException")) {
                assertTrue("Se produjo rollback por transacción", true);
            } else {
                fail("Excepción inesperada: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        } finally {
            try {
                testDA.open();
                testDA.removeUser(userEmail);
                testDA.close();
            } catch (Exception ex) {  }
        }
    }
}