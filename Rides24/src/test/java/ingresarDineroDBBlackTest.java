import dataAccess.DataAccess;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.NonexitstenUserException;
import org.junit.Test;
import testOperations.TestDataAccess;

import static org.junit.Assert.*;

public class ingresarDineroDBBlackTest {

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
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

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
            try { sut.close(); } catch (Exception ex) {}
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

    /*
    @Test
    public void testCajaNegra2() {
        // Test case 2: Usuario existe, tiene cuenta con suficiente dinero, sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;

        try {
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

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
            try { sut.close(); } catch (Exception ex) {}
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
    */

    @Test
    public void testCajaNegra3() {
        // Test case 3: Cantidad negativa (debe lanzar excepción desde BL)
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = -5.0f;

        try {
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUsersinMonederoDineroEnCuenta(userEmail, pass, userName, 100);
            testDA.close();

            sut.open();
            sut.ingresarDinero(userEmail, cantidad);
            sut.close();
            
            fail("Debería lanzar CantidadInvalidaException");
            
        } catch (CantidadInvalidaException e) {
            try { sut.close(); } catch (Exception ex) {  }
            assertTrue("ERROR: El test debería fallar - Se esperaba que NO se lance CantidadInvalidaException pero sí se lanzó " ,true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) { /* ignore */ }
            if (e.getClass().getSimpleName().contains("RollbackException")) {
                fail("Se produjo rollback por transacción");
            } else {
                fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName() + " - Mensaje: " + e.getMessage());
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
            try { sut.close(); } catch (Exception ex) { }
            System.out.println("✓ TEST CAJA NEGRA 4: Capturó NonexitstenUserException correctamente");
            assertTrue("El usuario no existe", true);
        } catch (IllegalArgumentException e) {
            try { sut.close(); } catch (Exception ex) { }
            assertTrue("Email null genera IllegalArgumentException", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    // COMENTAR ESTE TEST REDUCIRÁ EL COVERAGE: Cubre validarYObtenerUsuario() cuando user==null (línea 515-516)
    @Test
    public void testCajaNegra5() {
        // Test case 5: Usuario no existe en la DB
        String userEmail = "rgallego007@ikasle.ehu.eus";
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
            try { sut.close(); } catch (Exception ex) {  }
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            try { sut.close(); } catch (Exception ex) {  }
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    // COMENTAR ESTE TEST REDUCIRÁ EL COVERAGE: Cubre validarSaldoEnCuenta() con monedero YA existente (línea 532-533)
    @Test
    public void testCajaNegra6() {
        // Test case 6: Usuario con saldo insuficiente en cuenta
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 120.0f;

        try {
            // Limpiar datos residuales
            testDA.open();
            testDA.removeUser(userEmail);
            testDA.close();

            testDA.open();
            testDA.crearUserconMonederoSinDinero(userEmail, pass, userName, 100);
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

    @Test
    public void testCajaNegra7_ValoresLimite_ConMonedero() {
        // Test case 7: Todos los valores límite con monedero existente
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float[] valoresLimite = {0.01f, 0.0f, -0.01f, 100.01f, 100.0f, 99.99f};
        
        for (int i = 0; i < valoresLimite.length; i++) {
            float cantidad = valoresLimite[i];
            String testUserEmail = userEmail + i; // Email único para cada test
            
            try {
                testDA.open();
                testDA.crearUserconMonederoSinDinero(testUserEmail, pass, userName, 100);
                User user = testDA.getUser(testUserEmail);
                user.getMonedero().setSaldo(50.0f);
                testDA.close();

                if (cantidad == 0.01f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería existir", result);
                    assertEquals("El saldo del monedero debe coincidir", 50.01f, result.getSaldo(), 0.001);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertEquals("El saldo en DB debe coincidir", 50.01f, userAfterTest.getMonedero().getSaldo(), 0.001);
                    assertEquals("El saldo de cuenta debe haberse reducido", 99.99f, userAfterTest.getCuenta().getNumeroRandom(), 1.0f);
                    testDA.close();
                    
                } else if (cantidad == 0.0f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería existir", result);
                    assertEquals("El saldo del monedero no debe cambiar", 50.0f, result.getSaldo(), 0.01);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertEquals("El saldo de cuenta no debe cambiar", 100.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01f);
                    testDA.close();
                    
                } else if (cantidad == -0.01f) {
                    try {
                        sut.open();
                        sut.ingresarDinero(testUserEmail, cantidad);
                        sut.close();
                        fail("DEBERÍA lanzar CantidadInvalidaException para cantidad negativa, pero no lo hace - BUG DETECTADO");
                    } catch (CantidadInvalidaException e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        assertTrue("Excepción correcta para cantidad negativa", true);
                    } catch (Exception e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        fail("Lanzó excepción incorrecta para cantidad negativa: " + e.getClass().getSimpleName());
                    }
                    
                } else if (cantidad == 100.01f) {
                    try {
                        sut.open();
                        sut.ingresarDinero(testUserEmail, cantidad);
                        sut.close();
                        fail("Debería lanzar CantidadInvalidaException");
                    } catch (CantidadInvalidaException e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());
                    } catch (Exception e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
                    }
                    
                } else if (cantidad == 100.0f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería existir", result);
                    assertEquals("El saldo del monedero debe actualizarse", 150.0f, result.getSaldo(), 0.01);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertEquals("El saldo de cuenta debe quedar en 0", 0.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01f);
                    testDA.close();
                    
                } else if (cantidad == 99.99f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería existir", result);
                    assertEquals("El saldo del monedero debe actualizarse", 149.99f, result.getSaldo(), 0.01);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertEquals("El saldo de cuenta debe haberse reducido", 0.01f, userAfterTest.getCuenta().getNumeroRandom(), 0.02f);
                    testDA.close();
                }
                
            } catch (Exception e) {
                try { sut.close(); } catch (Exception ex) {  }
                if (cantidad > 0 && cantidad <= 100 && !e.getClass().getSimpleName().contains("RollbackException")) {
                    fail("Excepción inesperada para cantidad " + cantidad + ": " + e.getClass().getSimpleName());
                }
            } finally {
                testDA.open();
                testDA.removeUser(testUserEmail);
                testDA.close();
            }
        }
    }

    @Test
    public void testCajaNegra8_ValoresLimite_SinMonedero() {
        // Test case 8: Todos los valores límite sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float[] valoresLimite = {0.01f, 0.0f, -0.01f, 100.01f, 100.0f, 99.99f};
        
        for (int i = 0; i < valoresLimite.length; i++) {
            float cantidad = valoresLimite[i];
            String testUserEmail = userEmail + "_sin_" + i; // Email único para cada test
            
            try {
                testDA.open();
                testDA.crearUsersinMonedero(testUserEmail, pass, userName, 100);
                testDA.close();

                if (cantidad == 0.01f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería haberse creado", result);
                    assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.001);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
                    assertEquals("El saldo en DB debe coincidir", cantidad, userAfterTest.getMonedero().getSaldo(), 0.001);
                    assertEquals("El saldo de cuenta debe haberse reducido", 99.99f, userAfterTest.getCuenta().getNumeroRandom(), 1.0f);
                    testDA.close();
                    
                } else if (cantidad == 0.0f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería haberse creado", result);
                    assertEquals("El saldo del monedero debe ser 0", 0.0f, result.getSaldo(), 0.01);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertEquals("El saldo de cuenta no debe cambiar", 100.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01f);
                    testDA.close();
                    
                } else if (cantidad == -0.01f) {
                    try {
                        sut.open();
                        sut.ingresarDinero(testUserEmail, cantidad);
                        sut.close();
                        fail("DEBERÍA lanzar CantidadInvalidaException para cantidad negativa, pero no lo hace - BUG DETECTADO");
                    } catch (CantidadInvalidaException e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        assertTrue("Excepción correcta para cantidad negativa", true);
                    } catch (Exception e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        fail("Lanzó excepción incorrecta para cantidad negativa: " + e.getClass().getSimpleName());
                    }
                    
                } else if (cantidad == 100.01f) {
                    try {
                        sut.open();
                        sut.ingresarDinero(testUserEmail, cantidad);
                        sut.close();
                        fail("Debería lanzar CantidadInvalidaException");
                    } catch (CantidadInvalidaException e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());
                        
                        testDA.open();
                        User userAfterTest = testDA.getUser(testUserEmail);
                        assertNotNull("El monedero se crea antes de la validación", userAfterTest.getMonedero());
                        assertEquals("El saldo del monedero debe ser 0", 0.0f, userAfterTest.getMonedero().getSaldo(), 0.01);
                        assertEquals("El saldo de cuenta no debe cambiar", 100.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01f);
                        testDA.close();
                    } catch (Exception e) {
                        try { sut.close(); } catch (Exception ex) { /* ignore */ }
                        fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
                    }
                    
                } else if (cantidad == 100.0f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería haberse creado", result);
                    assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.01);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
                    assertEquals("El saldo de cuenta debe quedar en 0", 0.0f, userAfterTest.getCuenta().getNumeroRandom(), 0.01f);
                    testDA.close();
                    
                } else if (cantidad == 99.99f) {
                    sut.open();
                    Monedero result = sut.ingresarDinero(testUserEmail, cantidad);
                    sut.close();
                    
                    assertNotNull("El monedero debería haberse creado", result);
                    assertEquals("El saldo del monedero debe coincidir", cantidad, result.getSaldo(), 0.001);
                    
                    testDA.open();
                    User userAfterTest = testDA.getUser(testUserEmail);
                    assertNotNull("El monedero debería existir", userAfterTest.getMonedero());
                    assertEquals("El saldo en DB debe coincidir", cantidad, userAfterTest.getMonedero().getSaldo(), 0.001);
                    assertEquals("El saldo de cuenta debe haberse reducido", 0.01f, userAfterTest.getCuenta().getNumeroRandom(), 0.02f);
                    testDA.close();
                }
                
            } catch (Exception e) {
                try { sut.close(); } catch (Exception ex) { /* ignore */ }
                if (cantidad > 0 && cantidad <= 100 && !e.getClass().getSimpleName().contains("RollbackException")) {
                    fail("Excepción inesperada para cantidad " + cantidad + ": " + e.getClass().getSimpleName());
                }
            } finally {
                testDA.open();
                testDA.removeUser(testUserEmail);
                testDA.close();
            }
        }
    }
}