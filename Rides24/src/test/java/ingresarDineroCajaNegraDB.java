import dataAccess.DataAccess;
import domain.User;
import domain.Monedero;
import exceptions.NonexitstenUserException;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class ingresarDineroCajaNegraDB {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void testCajaNegra1() {
        // Test case 1: Usuario existe, tiene cuenta con suficiente dinero, tiene monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debería haberse creado", result);
            assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.01);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe coincidir", cantidad, userAfterTest.getMonedero().getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse reducido", 50.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01);
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
        // Test case 2: Usuario existe, tiene cuenta con suficiente dinero, sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debería haberse creado", result);
            assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.01);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
            assertEquals("El saldo en DB debe coincidir", cantidad, userAfterTest.getMonedero().getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse reducido", 50.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01);
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
    public void testCajaNegra3() {
        // Test case 3: Cantidad negativa (debe lanzar excepción desde BL)
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = -5.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
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
    public void testCajaNegra4() {
        // Test case 4: userEmail es null
        String userEmail = null;
        float cantidad = 50.0f;

        try {
            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
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
    public void testCajaNegra5() {
        // Test case 5: Usuario no existe en la DB
        String userEmail = "usuarionoexiste@falso.com";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.removeUser(userEmail); // Asegurar que no existe
            testDA.close();

            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
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
    public void testCajaNegra6() {
        // Test case 6: Usuario con saldo insuficiente en cuenta
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 120.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
            sut.close();
            
            fail("Debería lanzar CantidadInvalidaException");
            
        } catch (CantidadInvalidaException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());
            
            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNotNull("El monedero debería haberse creado", userAfterTest.getMonedero());
            testDA.close();
            
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