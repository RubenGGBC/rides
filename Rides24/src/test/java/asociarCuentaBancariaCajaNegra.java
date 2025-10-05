import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.Driver;
import domain.Monedero;
import domain.User;
import exceptions.NonexitstenUserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.*;

import static org.junit.Assert.*;

public class asociarCuentaBancariaCajaNegra {
    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test
    public void testCajaNegra1_UsuarioSinMonedero() {
        // TEST CAJA NEGRA - Usuario existe SIN monedero, se crea uno nuevo
        String userEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        // Usuario NO tiene monedero inicialmente
        Driver falsoDriver = new Driver(userEmail, "UserF");
        // Driver NO tiene monedero inicialmente

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));

        try {
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            assertNotNull("El usuario debe tener cuenta", usuarioFalso.getCuenta());
            assertNotNull("El usuario debe tener monedero creado", usuarioFalso.getMonedero());
            assertNotNull("El driver debe tener cuenta", falsoDriver.getCuenta());
            assertNotNull("El driver debe tener monedero", falsoDriver.getMonedero());
            assertEquals("La cuenta debe coincidir", cuentaBancaria, usuarioFalso.getCuenta());
            assertEquals("Las cuentas deben ser iguales", falsoDriver.getCuenta(), usuarioFalso.getCuenta());
            assertEquals("Los monederos deben ser iguales", falsoDriver.getMonedero(), usuarioFalso.getMonedero());
            assertNotNull("El monedero retornado no debe ser null", monederoRetornado);

            System.out.println("✓ TEST CAJA NEGRA 1: Usuario sin monedero - monedero creado");

        } catch (Exception e) {
            System.out.println("✗ TEST CAJA NEGRA 1: Excepción: " + e.getClass().getName());
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra2_EmailNull() {
        // TEST CAJA NEGRA #1: Clase 2 - userEmail == null
        String userEmail = null;
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);

        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            fail("Debería lanzar excepción cuando userEmail es null");
        } catch (NonexitstenUserException e) {
            System.out.println("✓ TEST CAJA NEGRA 2: Capturó NonexitstenUserException (email null)");
            assertTrue("El usuario no existe", true);
        } catch (IllegalArgumentException e) {
            System.out.println("✓ TEST CAJA NEGRA 2: Capturó IllegalArgumentException (email null)");
            assertTrue("Email null genera IllegalArgumentException", true);
        } catch (Exception e) {
            System.out.println("✗ TEST CAJA NEGRA 2: Excepción incorrecta: " + e.getClass().getName());
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
    
    @Test
    public void testCajaNegra3_CuentaBancariaNull() {
        // TEST CAJA NEGRA #2: Clases 1, 4 - cuentaBancaria == null
        String userEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = null;
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        Driver falsoDriver = new Driver(userEmail, "UserF");

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));

        try {
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            assertNull("La cuenta del usuario debe ser null", usuarioFalso.getCuenta());
            assertNotNull("El monedero debe haberse creado", usuarioFalso.getMonedero());
            assertNull("La cuenta del driver debe ser null", falsoDriver.getCuenta());
            assertNotNull("El monedero del driver debe existir", falsoDriver.getMonedero());
            assertEquals("Los monederos deben ser iguales", falsoDriver.getMonedero(), usuarioFalso.getMonedero());
            assertNotNull("El monedero retornado no debe ser null", monederoRetornado);

            System.out.println("✓ TEST CAJA NEGRA 3: cuentaBancaria null - no se asigna cuenta");

        } catch (Exception e) {
            System.out.println("✗ TEST CAJA NEGRA 3: Excepción: " + e.getClass().getName());
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra4_UsuarioNoExiste() {
        // TEST CAJA NEGRA #3: Clases 1, 3, 6 - Usuario NO existe
        String userEmail = "usuarionoexiste@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);

        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            System.out.println("✓ TEST CAJA NEGRA 4: Capturó NonexitstenUserException (usuario no existe)");
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            System.out.println("✗ TEST CAJA NEGRA 4: Excepción incorrecta: " + e.getClass().getName());
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra5_UsuarioConMonedero() {
        // TEST CAJA NEGRA #4: Clases 1, 3, 5, 7 - Usuario existe CON monedero
        String userEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        Monedero monederoExistente = new Monedero(userEmail + "_wallet");
        monederoExistente.setSaldo(50.0f);
        usuarioFalso.setMonedero(monederoExistente);
        Driver falsoDriver = new Driver(userEmail, "UserF");
        falsoDriver.setMonedero(monederoExistente);

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));

        try {
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            assertNotNull("El usuario debe tener cuenta", usuarioFalso.getCuenta());
            assertNotNull("El usuario debe tener monedero", usuarioFalso.getMonedero());
            assertNotNull("El driver debe tener cuenta", falsoDriver.getCuenta());
            assertNotNull("El driver debe tener monedero", falsoDriver.getMonedero());
            assertEquals("La cuenta debe coincidir", cuentaBancaria, usuarioFalso.getCuenta());
            assertEquals("Las cuentas deben ser iguales", falsoDriver.getCuenta(), usuarioFalso.getCuenta());
            assertEquals("Los monederos deben ser iguales", falsoDriver.getMonedero(), usuarioFalso.getMonedero());
            assertEquals("El saldo debe mantenerse", 50.0f, usuarioFalso.getMonedero().getSaldo(), 0.01f);
            assertNotNull("El monedero retornado no debe ser null", monederoRetornado);

            System.out.println("✓ TEST CAJA NEGRA 5: Usuario con monedero - cuenta asignada");

        } catch (Exception e) {
            System.out.println("✗ TEST CAJA NEGRA 5: Excepción: " + e.getClass().getName());
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testCajaNegra6_UsuarioSinDriver() {
        // TEST ADICIONAL: Usuario existe pero NO hay driver asociado
        // El código actual de DataAccess causa NullPointerException cuando no hay driver
        String userEmail = "usuariofalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Collections.emptyList());

        try {
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            fail("Debería lanzar NullPointerException porque el código no maneja driver == null");

        } catch (NullPointerException e) {
            System.out.println("✓ TEST CAJA NEGRA 6: Capturó NullPointerException esperado (driver es null)");
            assertTrue("El código actual no maneja driver == null correctamente", true);
        } catch (Exception e) {
            System.out.println("✗ TEST CAJA NEGRA 6: Excepción incorrecta: " + e.getClass().getName());
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }
}