import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.Monedero;
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
        // TEST CAJA NEGRA #1: Clase de equivalencia 2 - userEmail == null
        String userEmail = null;
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            fail("Debería lanzar excepción (userEmail es null)");

        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✓ TEST CAJA NEGRA 1: Capturó NonexitstenUserException correctamente");
            assertTrue("userEmail null genera excepción", true);
        } catch (IllegalArgumentException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✓ TEST CAJA NEGRA 1: Capturó IllegalArgumentException correctamente");
            assertTrue("userEmail null genera IllegalArgumentException", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST CAJA NEGRA 1: Excepción: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra2() {
        // TEST CAJA NEGRA #2: Clase de equivalencia 1, 4 - cuentaBancaria == null
        String userEmail = "usuario@test.com";
        String pass = "password123";
        String userName = "UserTest";
        CuentaBancaria cuentaBancaria = null;

        try {
            // Crear usuario con monedero
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 500);
            testDA.close();

            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            // No debería lanzar excepción, simplemente no se asigna cuenta
            assertNotNull("El monedero debe existir", monederoRetornado);
            assertNull("La cuenta bancaria debe ser null", monederoRetornado.getCuentaBancaria());

            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNull("La cuenta no debería estar asociada", userAfterTest.getCuenta());
            assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
            testDA.close();

            System.out.println("✓ TEST CAJA NEGRA 2: Completado exitosamente - cuentaBancaria null");

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST CAJA NEGRA 2: Excepción: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        } finally {
            try {
                testDA.open();
                testDA.removeUser(userEmail);
                testDA.close();
            } catch (Exception ex) { /* ignore */ }
        }
    }

    @Test
    public void testCajaNegra3() {
        // TEST CAJA NEGRA #3: Clases 1, 3, 6 - Usuario NO existe en DB
        String userEmail = "usuarionoexiste@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        try {
            // Asegurar que el usuario no existe
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            fail("Debería lanzar NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✓ TEST CAJA NEGRA 3: Capturó NonexitstenUserException correctamente");
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST CAJA NEGRA 3: Excepción: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4() {
        // TEST CAJA NEGRA #4: Clases 1, 3, 5, 7 - Usuario existe con monedero
        String userEmail = "usuarioConMonedero@test.com";
        String pass = "password123";
        String userName = "UserConMonedero";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        float saldoInicial = 150.0f;

        try {
            // Crear usuario con monedero
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 500);
            User user = testDA.getUser(userEmail);
            user.getMonedero().setSaldo(saldoInicial);
            testDA.close();

            // Asociar cuenta bancaria
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            // Verificar que se devuelve el monedero con la cuenta bancaria asignada
            assertNotNull("El monedero no debe ser null", monederoRetornado);
            assertNotNull("La cuenta bancaria debe estar asignada", monederoRetornado.getCuentaBancaria());
            assertEquals("El número de tarjeta debe coincidir", "1234567890", monederoRetornado.getCuentaBancaria().getNumerotarjeta());
            assertEquals("El saldo debe mantenerse", saldoInicial, monederoRetornado.getSaldo(), 0.01f);

            // Verificar en la DB
            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNotNull("La cuenta debería estar asociada", userAfterTest.getCuenta());
            assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
            assertEquals("La cuenta debe coincidir", cuentaBancaria.getNumerotarjeta(), userAfterTest.getCuenta().getNumerotarjeta());
            assertEquals("El saldo del monedero debe mantenerse", saldoInicial, userAfterTest.getMonedero().getSaldo(), 0.01f);
            testDA.close();

            System.out.println("✓ TEST CAJA NEGRA 4: Completado exitosamente");

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST CAJA NEGRA 4: Excepción: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Excepción inesperada: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
            try {
                testDA.open();
                testDA.removeUser(userEmail);
                testDA.close();
            } catch (Exception ex) { /* ignore */ }
        }
    }

    @Test
    public void testCajaNegra5() {
        // TEST CAJA NEGRA #5: Clases 1, 3, 5, 8 - Usuario existe SIN monedero
        String userEmail = "usuarioSinMonedero@test.com";
        String pass = "password123";
        String userName = "UserSinMonedero";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("9876543210");

        try {
            // Crear usuario SIN monedero
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserSinMonedero(userEmail, pass, userName, 500);
            testDA.close();

            // Verificar que el usuario no tiene monedero
            testDA.open();
            User userAntes = testDA.getUser(userEmail);
            assertNull("El usuario NO debe tener monedero inicialmente", userAntes.getMonedero());
            testDA.close();

            // Asociar cuenta bancaria
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            // Verificar que se devuelve un nuevo monedero con la cuenta bancaria asignada
            assertNotNull("El monedero no debe ser null", monederoRetornado);
            assertNotNull("La cuenta bancaria debe estar asignada", monederoRetornado.getCuentaBancaria());
            assertEquals("El número de tarjeta debe coincidir", "9876543210", monederoRetornado.getCuentaBancaria().getNumerotarjeta());

            // Verificar en la DB
            testDA.open();
            User userAfterTest = testDA.getUser(userEmail);
            assertNotNull("El usuario debería existir", userAfterTest);
            assertNotNull("El monedero debe haberse creado", userAfterTest.getMonedero());
            assertNotNull("La cuenta debería estar asociada", userAfterTest.getCuenta());
            assertEquals("La nueva cuenta debe coincidir", cuentaBancaria.getNumerotarjeta(), userAfterTest.getCuenta().getNumerotarjeta());
            assertEquals("El monedero debe tener la cuenta asignada", "9876543210", userAfterTest.getMonedero().getCuentaBancaria().getNumerotarjeta());
            testDA.close();

            System.out.println("✓ TEST CAJA NEGRA 5: Completado exitosamente - Usuario sin monedero");

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST CAJA NEGRA 5: Excepción: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Excepción inesperada: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
            try {
                testDA.open();
                testDA.removeUser(userEmail);
                testDA.close();
            } catch (Exception ex) { /* ignore */ }
        }
    }
}