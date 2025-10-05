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

public class asociasCuentaBancariaCajaBlanca {
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
    public void asociarCuentaBancariaTest1_UsuarioNoExiste() {
        // Camino 1: (1-4)-IF1(T)-5-EXC1-End
        // Usuario NO existe en DB
        String userEmail = "userfalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");

        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);

        try {
            sut.open();
            sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            System.out.println("✓ TEST 1: Capturó NonexitstenUserException correctamente");
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            System.out.println("✗ TEST 1: Excepción inesperada: " + e.getClass().getName());
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void asociarCuentaBancariaTest2_UsuarioConMonedero() {
        // Camino 2: (1-4)-IF1(F)-6-IF2(F)-(12-17)-18-End
        // Usuario existe CON monedero
        String userEmail = "userfalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        Monedero monederoExistente = new Monedero(userEmail + "_wallet");
        monederoExistente.setSaldo(100.0f);
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

            // Verificaciones
            assertNotNull("El monedero retornado no debe ser null", monederoRetornado);
            assertNotNull("El usuario debe tener cuenta", usuarioFalso.getCuenta());
            assertNotNull("El usuario debe tener monedero", usuarioFalso.getMonedero());
            assertNotNull("El driver debe tener cuenta", falsoDriver.getCuenta());
            assertNotNull("El driver debe tener monedero", falsoDriver.getMonedero());
            assertEquals("La cuenta del usuario debe coincidir", cuentaBancaria, usuarioFalso.getCuenta());
            assertEquals("La cuenta del driver debe coincidir con la del usuario", falsoDriver.getCuenta(), usuarioFalso.getCuenta());
            assertEquals("El monedero debe ser el mismo", falsoDriver.getMonedero(), usuarioFalso.getMonedero());
            assertEquals("El saldo debe mantenerse", 100.0f, monederoRetornado.getSaldo(), 0.01f);

            System.out.println("✓ TEST 2: Completado - Usuario con monedero");

        } catch (Exception e) {
            System.out.println("✗ TEST 2: Excepción inesperada: " + e.getClass().getName());
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }


    @Test
    public void asociarCuentaBancariaTest3_UsuarioSinMonedero() {
        // Camino 3: (1-4)-IF1(F)-6-IF2(T)-(7-11)-(12-17)-18-End
        // Usuario existe SIN monedero
        String userEmail = "userfalso@falso.com";
        CuentaBancaria cuentaBancaria = new CuentaBancaria("1234567890");
        User usuarioFalso = new User(userEmail, "userfalso", false, "UserF");
        // NO se asigna monedero al usuario inicialmente
        Driver falsoDriver = new Driver(userEmail, "UserF");
        // NO se asigna monedero al driver inicialmente

        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        TypedQuery<Driver> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(db.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)).thenReturn(typedQuery);
        Mockito.when(typedQuery.getResultList()).thenReturn(java.util.Arrays.asList(falsoDriver));

        try {
            sut.open();
            Monedero monederoRetornado = sut.asociarCuentaBancaria(userEmail, cuentaBancaria);
            sut.close();

            // Verificaciones - debe crear un nuevo monedero
            assertNotNull("El monedero retornado no debe ser null", monederoRetornado);
            assertNotNull("El usuario debe tener cuenta", usuarioFalso.getCuenta());
            assertNotNull("El usuario debe tener monedero creado", usuarioFalso.getMonedero());
            assertNotNull("El driver debe tener cuenta", falsoDriver.getCuenta());
            assertNotNull("El driver debe tener monedero creado", falsoDriver.getMonedero());
            assertEquals("La cuenta del usuario debe coincidir", cuentaBancaria, usuarioFalso.getCuenta());
            assertEquals("La cuenta del driver debe coincidir con la del usuario", falsoDriver.getCuenta(), usuarioFalso.getCuenta());
            assertEquals("El monedero debe ser compartido", falsoDriver.getMonedero(), usuarioFalso.getMonedero());
            assertEquals("El saldo inicial debe ser 0", 0.0f, monederoRetornado.getSaldo(), 0.01f);

            System.out.println("✓ TEST 3: Completado - Usuario sin monedero, monedero creado");

        } catch (Exception e) {
            System.out.println("✗ TEST 3: Excepción inesperada: " + e.getClass().getName());
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }


}
