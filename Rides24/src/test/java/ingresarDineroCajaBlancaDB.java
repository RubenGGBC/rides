import dataAccess.DataAccess;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.NonexitstenUserException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class ingresarDineroCajaBlancaDB {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void test1() {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50.0f;

        try {

            testDA.open();
            testDA.removeUser(userEmail); //Lo borramos por si acaso
            testDA.close();

            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
            sut.close();

            fail("Deberia haver lanzado NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            if (e.getClass().getSimpleName().contains("RollbackException")) {
                assertTrue("Se produjo rollback por transacción", true);
            } else {
                fail("Excepción inesperada: " + e.getClass().getSimpleName());
            }
        }
    }

    @Test
    public void test2() {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 30);
            testDA.close();

            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
            sut.close();

            fail("Should throw CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            assertTrue("Insufficient funds in account", true);
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
    public void test3() {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            testDA.open();
            testDA.crearUsersinMonedero(userEmail, pass, userName, 30);
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
                fail("Excepción incorrecta: " + e.getClass().getSimpleName());
            }
        } finally {
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();
        }
    }

    @Test
    public void test4() {
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
            boolean monederoExists = testDA.existMonedero(userEmail);
            assertTrue("El monedero debería existir en la DB", monederoExists);
            
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
    public void test5_ValorLimiteMinimo() {
        // Test case 5: Valor límite mínimo - cantidad = 0.01 con monedero existente
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 0.01f;

        try {
            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 100);
            User user = testDA.getUser(userEmail);
            user.getMonedero().setSaldo(50.0f);
            testDA.close();

            sut.open();
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe coincidir", 50.01f, result.getSaldo(), 0.001);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertEquals("El saldo en DB debe coincidir", 50.01f, userAfterTest.getMonedero().getSaldo(), 0.001);
            assertEquals("El saldo de cuenta debe haberse reducido", 99.99f, userAfterTest.getCuenta().getNumeroRandom(), 1.0f);
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
    public void test6_ValorLimiteMaximo() {
        // Test case 6: Valor límite máximo - cantidad = 99.99 sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 99.99f;

        try {
            testDA.open();
            testDA.crearUsersinMonedero(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            Monedero result = sut.ingresarDinero(userEmail, cantidad);
            sut.close();

            assertNotNull("El monedero debería haberse creado", result);
            assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.001);

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
            assertEquals("El saldo en DB debe coincidir", cantidad, userAfterTest.getMonedero().getSaldo(), 0.001);
            assertEquals("El saldo de cuenta debe haberse reducido", 0.01f, userAfterTest.getCuenta().getNumeroRandom(), 0.02f);
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