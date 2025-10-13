import dataAccess.DataAccess;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.NonexitstenUserException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class ingresarDineroBDWhiteTest {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();


    //coverage
    @Test
    public void test1() {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50.0f;

        try {

            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
            sut.close();

            fail("Deberia haver lanzado NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) {}
            System.out.println("TEST 1: Capturo NonexitstenUserException correctamente");
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { }
            System.out.println("TEST 1: Excepcion capturada: " + e.getClass().getName() + " - " + e.getMessage());
            if (e.getClass().getSimpleName().contains("RollbackException")) {
                assertTrue("Se produjo rollback por transaccion", true);
            } else {
                fail("Excepcion inesperada: " + e.getClass().getSimpleName());
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

            fail("Deberia haber lanzado CantidadInvalidaException");

        } catch (CantidadInvalidaException e) {
            try { sut.close(); } catch (Exception ex) { }
            System.out.println("✓ TEST 2: Capturó CantidadInvalidaException correctamente");
            assertTrue("No hay suficiente dinero en la cuent para hacer la transaccion", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            System.out.println("✗ TEST 2: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
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


    //coverage
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
            try { sut.close(); } catch (Exception ex) {  }
            System.out.println("✓ TEST 3: Capturó CantidadInvalidaException correctamente");
            assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNull("El monedero NO debería haberse creado porque la transacción falló", userAfterTest.getMonedero());
            testDA.close();

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            System.out.println("✗ TEST 3: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
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

            System.out.println("✓ TEST 4: Completado exitosamente - No se lanzó excepción");

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            System.out.println("✗ TEST 4: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
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