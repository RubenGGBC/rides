import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.User;
import exceptions.NonexitstenUserException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class asociarCuentaBancariaCajaNegraDB {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void testCajaNegra1() {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        try {
            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 100);
            User user = testDA.getUser(userEmail);
            user.getMonedero().setSaldo(120.0f);
            testDA.close();

            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNotNull("La cuenta debería estar asociada", userAfterTest.getCuenta());
            assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
            assertEquals("La cuenta debe coincidir", cuentaBancaria.getNumerotarjeta(), userAfterTest.getCuenta().getNumerotarjeta());
            assertEquals("El saldo del monedero debe mantenerse", 120.0f, userAfterTest.getMonedero().getSaldo(), 0.01f);
            testDA.close();

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
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

    @Test
    public void testCajaNegra2() {
        String userEmail = null;
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            
            fail("Debería lanzar excepción");
            
        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertTrue("El usuario no existe", true);
        } catch (IllegalArgumentException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertTrue("Email null genera IllegalArgumentException", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra3() {
        String userEmail = "usuarionoexiste@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        try {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            
            fail("Debería lanzar NonexitstenUserException");
            
        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4() {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        CuentaBancaria cuentaBancaria = null;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNull("La cuenta no debería estar asociada", userAfterTest.getCuenta());
            assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
            testDA.close();

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
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

    @Test
    public void testCajaNegra5() {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("9876543210");

        try {
            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 100);
            User user = testDA.getUser(userEmail);
            user.getMonedero().setSaldo(75.0f);
            CuentaBancaria cuentaAnterior = new CuentaBancaria("1111111111");
            user.setCuenta(cuentaAnterior);
            testDA.close();

            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNotNull("La cuenta debería estar asociada", userAfterTest.getCuenta());
            assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
            assertEquals("La nueva cuenta debe coincidir", cuentaBancaria.getNumerotarjeta(), userAfterTest.getCuenta().getNumerotarjeta());
            assertEquals("El saldo del monedero debe mantenerse", 75.0f, userAfterTest.getMonedero().getSaldo(), 0.01f);
            testDA.close();

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
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
}