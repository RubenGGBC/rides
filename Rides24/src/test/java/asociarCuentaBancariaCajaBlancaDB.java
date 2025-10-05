import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.Monedero;
import domain.User;
import exceptions.NonexitstenUserException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class asociarCuentaBancariaCajaBlancaDB {

    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    public void test1() {
        // Camino 1: (1-4)-IF1(T)-5-EXC1-End
        // El email no corresponde a ningún usuario de la DB
        String userEmail = "usuarioFalso@gmail.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        try {
            // Asegurar que el usuario no existe en la DB
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            // Intentar asociar cuenta bancaria a usuario inexistente
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            fail("Debería lanzar NonexitstenUserException");

        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✓ TEST 1: Capturó NonexitstenUserException correctamente");
            assertTrue("El usuario no existe en la DB", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST 1: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test2() {
        // Camino 2: (1-4)-IF1(F)-6-IF2(F)-(12-17)-18-End
        // El email corresponde a un usuario de la DB que tiene monedero
        String userEmail = "usuarioConMonedero@gmail.com";
        String pass = "password123";
        String userName = "UserConMonedero";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        float saldoInicial = 100.0f;

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
            User userVerificacion = testDA.getUser(userEmail);
            assertNotNull("La cuenta del usuario debe estar asignada", userVerificacion.getCuenta());
            assertEquals("La cuenta bancaria debe coincidir", "1234567890", userVerificacion.getCuenta().getNumerotarjeta());
            assertEquals("El saldo del monedero debe mantenerse", saldoInicial, userVerificacion.getMonedero().getSaldo(), 0.01f);
            testDA.close();

            System.out.println("✓ TEST 2: Completado exitosamente - Usuario con monedero");

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST 2: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
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
    public void test3() {
        // Camino 3: (1-4)-IF1(F)-6-IF2(T)-(7-11)-(12-17)-18-End
        // El email corresponde a un usuario de la DB que NO tiene monedero
        String userEmail = "usuarioSinMonedero@gmail.com";
        String pass = "password123";
        String userName = "UserSinMonedero";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

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
            assertEquals("El número de tarjeta debe coincidir", "1234567890", monederoRetornado.getCuentaBancaria().getNumerotarjeta());
            assertEquals("El saldo inicial debe ser 0", 0.0f, monederoRetornado.getSaldo(), 0.01f);

            // Verificar en la DB que se creó el monedero y se asignó la cuenta
            testDA.open();
            User userVerificacion = testDA.getUser(userEmail);
            assertNotNull("El usuario debe tener monedero ahora", userVerificacion.getMonedero());
            assertNotNull("La cuenta del usuario debe estar asignada", userVerificacion.getCuenta());
            assertEquals("La cuenta bancaria debe coincidir", "1234567890", userVerificacion.getCuenta().getNumerotarjeta());
            assertEquals("El monedero debe tener la cuenta asignada", "1234567890", userVerificacion.getMonedero().getCuentaBancaria().getNumerotarjeta());
            testDA.close();

            System.out.println("✓ TEST 3: Completado exitosamente - Usuario sin monedero, monedero creado");

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST 3: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
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
    public void test4() {
        // TEST CAJA NEGRA #2: userEmail == null
        // Clases cubiertas: 2
        String userEmail = null;
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            fail("Debería lanzar excepción (userEmail es null)");

        } catch (NonexitstenUserException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✓ TEST 4: Capturó NonexitstenUserException correctamente (userEmail null)");
            assertTrue("userEmail null genera excepción", true);
        } catch (IllegalArgumentException e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✓ TEST 4: Capturó IllegalArgumentException correctamente (userEmail null)");
            assertTrue("userEmail null genera IllegalArgumentException", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST 4: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test5() {
        // TEST CAJA NEGRA #4: cuentaBancaria == null
        // Clases cubiertas: 1, 4
        String userEmail = "usuarioTest@gmail.com";
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

            // Asociar cuenta bancaria null
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            // No debería lanzar excepción, simplemente no se asigna cuenta
            assertNotNull("El monedero debe existir", monederoRetornado);
            assertNull("La cuenta bancaria debe ser null", monederoRetornado.getCuentaBancaria());

            // Verificar en DB
            testDA.open();
            User userVerificacion = testDA.getUser(userEmail);
            assertNull("La cuenta del usuario debe ser null", userVerificacion.getCuenta());
            testDA.close();

            System.out.println("✓ TEST 5: Completado exitosamente - cuentaBancaria null");

        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            System.out.println("✗ TEST 5: Excepción capturada: " + e.getClass().getName() + " - " + e.getMessage());
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
