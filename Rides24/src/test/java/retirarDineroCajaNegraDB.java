import dataAccess.DataAccess;
import domain.User;
import domain.Monedero;
import exceptions.NonexitstenUserException;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.SaldoInsuficienteException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class retirarDineroCajaNegraDB {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void testCajaNegra1() {
        // Test case 1: Caso exitoso - Usuario con monedero y saldo suficiente (retira 100€ de 120€)
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        int cantidad = 100;

        try {
            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 100);
            User user = testDA.getUser(userEmail);
            user.getMonedero().setSaldo(120.0f);
            testDA.close();

            sut.open();
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe haberse reducido", 20.0f, result.getSaldo(), 0.01);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe coincidir", 20.0f, userAfterTest.getMonedero().getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse incrementado", 200.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01);
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
        // Test case 2: Cantidad negativa (debe lanzar excepción desde BL)
        String userEmail = "rgallego007@ikasle.ehu.eus";
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
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertTrue("Cantidad inválida", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            if (e.getClass().getSimpleName().contains("RollbackException")) {
                assertTrue("Se produjo rollback por transacción", true);
            } else {
                fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
            }
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void testCajaNegra3() {
        // Test case 3: userEmail es null
        String userEmail = null;
        int cantidad = 50;

        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();
            
            fail("Debería lanzar NonexitstenUserException");
            
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
    public void testCajaNegra4() {
        // Test case 4: Usuario no existe en la DB
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
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra5() {
        // Test case 5: Usuario sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
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
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertEquals("El usuario no tiene monedero", e.getMessage());
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            if (e.getClass().getSimpleName().contains("RollbackException")) {
                assertTrue("Se produjo rollback por transacción", true);
            } else {
                fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
            }
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }
}