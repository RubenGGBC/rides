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

public class retirarDineroBDBlackTest {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void testCajaNegra1() {// Usuario con monedero y saldo suficiente
        String userEmail = "test1_mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 100.0f;
        float saldoInicialMonedero = 150.0f;
        int saldoInicialCuenta = 500;

        try {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserConMonederoSaldoSuficiente(userEmail, pass, userName, saldoInicialCuenta, saldoInicialMonedero);
            testDA.close();

            sut.open();
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debe existir", result);
            float saldoEsperado = saldoInicialMonedero - cantidad;
            assertEquals("El saldo del monedero debe ser = viejoSaldo - cantidad", saldoEsperado, result.getSaldo(), 0.01);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe coincidir", saldoEsperado, userAfterTest.getMonedero().getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe aumentar", saldoInicialCuenta + (int)cantidad, userAfterTest.getCuenta().getNumeroRandom(), 0.01);
            testDA.close();


        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { }
            fail("No debería haber saltado excepción: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }
    //coverage

    @Test
    public void testCajaNegra2() {//Cantidad inválida (negativa)
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        int cantidad = -5;

        try {
            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 100);
            User user = testDA.getUser(userEmail);
            user.getMonedero().setSaldo(120.0f);
            testDA.close();

            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            try {
                sut.close();
            } catch (Exception ex) {  }
            assertTrue("Cantidad inválida", true);
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) {  }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void testCajaNegra3() {// userEmail es null
        String userEmail = null;
        int cantidad = 50;

        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            try {
                sut.close();
            } catch (Exception ex) { }
            assertTrue("El usuario no existe", true);
        } catch (IllegalArgumentException e) {
            try {
                sut.close();
            } catch (Exception ex) {  }
            assertTrue("Email null genera IllegalArgumentException", true);
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4() {//Usuario no existe en la DB
        String userEmail = "usuarionoexiste@falso.com";
        int cantidad = 50;

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
            } catch (Exception ex) { }
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra5() {// Usuario sin monedero
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        int cantidad = 50;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar MonederoNoExisteException");

        } catch (MonederoNoExisteException e) {
            try {
                sut.close();
            } catch (Exception ex) {}
            assertEquals("El usuario no tiene monedero", e.getMessage());
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) {  }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void testCajaNegra6() { // Saldo insuficiente en monedero
        String userEmail = "test6_mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 100.0f;
        float saldoMonedero = 50.0f; // Menor que cantidad
        int saldoCuenta = 500;

        try {
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            // Crear usuario con monedero pero saldo insuficiente
            testDA.open();
            testDA.crearUserConMonederoSaldoInsuficiente(userEmail, pass, userName, saldoCuenta, saldoMonedero);
            testDA.close();

            // Intentar retirar más dinero del que tiene
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar SaldoInsuficienteException");

        } catch (SaldoInsuficienteException e) {
            try {
                sut.close();
            } catch (Exception ex) { }
            assertEquals("Saldo insuficiente en el monedero", e.getMessage());
        } catch (Exception e) {
            try {
                sut.close();
            } catch (Exception ex) { }
            if (e.getClass().getSimpleName().contains("RollbackException")) {
                assertTrue("Se produjo rollback por transacción", true);
            } else {
                fail("Excepción inesperada: " + e.getClass().getSimpleName());
            }
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    // ==================== VALORES LÍMITE ====================

    @Test
    public void testValorLimite1() { // Cantidad excede saldo por 0.01 (150.01 vs 150.0)
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 150.01f;
        float saldoMonedero = 150.0f;
        int saldoCuenta = 500;

        try {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserConMonederoSaldoSuficiente(userEmail, pass, userName, saldoCuenta, saldoMonedero);
            testDA.close();

            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar SaldoInsuficienteException");

        } catch (SaldoInsuficienteException e) {
            try { sut.close(); } catch (Exception ex) { }
            assertEquals("Saldo insuficiente en el monedero", e.getMessage());
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void testValorLimite2() { // Cantidad exacta al saldo (150.0 vs 150.0)
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 150.0f;
        float saldoMonedero = 150.0f;
        int saldoCuenta = 500;

        try {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserConMonederoSaldoSuficiente(userEmail, pass, userName, saldoCuenta, saldoMonedero);
            testDA.close();

            sut.open();
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debe existir", result);
            assertEquals("El saldo del monedero debe ser 0", 0.0f, result.getSaldo(), 0.01);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe ser 0", 0.0f, userAfterTest.getMonedero().getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe aumentar", saldoCuenta + (int)cantidad, userAfterTest.getCuenta().getNumeroRandom(), 0.01);
            testDA.close();


        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("No debería haber saltado excepción: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void testValorLimite3() { // Cantidad justo por debajo del saldo (149.99 vs 150.0)
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 149.99f;
        float saldoMonedero = 150.0f;
        int saldoCuenta = 500;

        try {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserConMonederoSaldoSuficiente(userEmail, pass, userName, saldoCuenta, saldoMonedero);
            testDA.close();

            sut.open();
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debe existir", result);
            assertEquals("El saldo del monedero debe ser 0.01", 0.01f, result.getSaldo(), 0.001);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe ser 0.01", 0.01f, userAfterTest.getMonedero().getSaldo(), 0.001);
            testDA.close();


        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("No debería haber saltado excepción: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void testValorLimite4() { // Cantidad mínima positiva (0.01)
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 0.01f;
        float saldoMonedero = 150.0f;
        int saldoCuenta = 500;

        try {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserConMonederoSaldoSuficiente(userEmail, pass, userName, saldoCuenta, saldoMonedero);
            testDA.close();

            sut.open();
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debe existir", result);
            assertEquals("El saldo del monedero debe ser 149.99", 149.99f, result.getSaldo(), 0.001);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe ser 149.99", 149.99f, userAfterTest.getMonedero().getSaldo(), 0.001);
            testDA.close();


        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("No debería haber saltado excepción: " + e.getClass().getSimpleName());
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    //coverage
    @Test
    public void testValorLimite5() { // Cantidad = 0 (límite entre válido/inválido)
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = 0.0f;

        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            try { sut.close(); } catch (Exception ex) {  }
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }
    //coverage
    @Test
    public void testValorLimite6() { // Cantidad negativa mínima (-0.01)
        String userEmail = "testLimite6_mberasategui022@ikasle.ehu.eus";
        float cantidad = -0.01f;

        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();

            fail("Debería lanzar CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            try { sut.close(); } catch (Exception ex) {  }
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

}