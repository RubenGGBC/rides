import dataAccess.DataAccess;
import domain.*;
import exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class retirarDineroCajaBlanca {
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
    public void retirarDineroTest1_CantidadNegativa() {
        String userEmail = "userf@falso.com";
        float cantidad = -2;
        
        try {
            sut.open();
            sut.retirarDinero(userEmail, cantidad);
            sut.close();
            fail("Debería lanzar CantidadInvalidaException");
        } catch (CantidadInvalidaException e) {
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
            assertTrue("Cantidad negativa detectada correctamente", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void retirarDineroTest2() throws NonexitstenUserException, CantidadInvalidaException {
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50.0f;

        // Camino 1: usuario no esta en la db
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);

        try {
            sut.open();
            try {
                sut.retirarDinero(userEmail, cantidad);
            } catch (MonederoNoExisteException e) {
                throw new RuntimeException(e);
            } catch (SaldoInsuficienteException e) {
                throw new RuntimeException(e);
            }
            fail("Debería lanzar NonexitstenUserException");
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } finally {
            sut.close();
        }
    }

    @Test
    public void retirarDineroTest3() throws NonexitstenUserException, MonederoNoExisteException, CantidadInvalidaException {
        String useremail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50;
        User usuarioFalso = new User(useremail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(30); //La cuenta tiene 30 euros
        Mockito.when(db.find(User.class, useremail)).thenReturn(usuarioFalso);
        try {
            sut.open();
            Monedero monederoresult = sut.retirarDinero(useremail, cantidad);
            sut.close();
            fail("Si llega aqui no ha saltado la excepcion");


        } catch (MonederoNoExisteException e) {
            fail("Si llega aqui no ha saltado la excepcion");
        } catch (NonexitstenUserException e) {
            fail("Si llega aqui no ha saltado la excepcion");
        } catch (CantidadInvalidaException e) {
            assertTrue(true);
        } catch (SaldoInsuficienteException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void retirarDineroTest4_MonederoNoExiste() {
        String useremail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50;

        User usuarioFalso = new User(useremail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        cuentaFalsa.setNumeroRandom(100);
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.setMonedero(null); // Usuario sin monedero

        Mockito.when(db.find(User.class, useremail)).thenReturn(usuarioFalso);

        try {
            sut.open();
            Monedero result = sut.retirarDinero(useremail, cantidad);
            sut.close();
            fail("Debería lanzar MonederoNoExisteException");
        } catch (MonederoNoExisteException e) {
            assertEquals("El usuario no tiene monedero", e.getMessage());
            assertTrue("Monedero no existe detectado correctamente", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void retirarDineroTest5_CasoExitoso() {
        String useremail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 30;
        User usuarioFalso = new User(useremail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        cuentaFalsa.setNumeroRandom(100);
        Monedero monederoExistente = new Monedero(useremail + "_wallet");
        monederoExistente.setSaldo(50.0f);
        usuarioFalso.setMonedero(monederoExistente);
        
        Mockito.when(db.find(User.class, useremail)).thenReturn(usuarioFalso);
        
        try {
            sut.open();
            Monedero result = sut.retirarDinero(useremail, cantidad);
            sut.close();
            
            assertEquals(20.0f, result.getSaldo(), 0.01f);
            assertEquals(130, usuarioFalso.getCuenta().getNumeroRandom());
            assertNotNull(result);
            assertTrue("Dinero retirado correctamente", true);
            
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }
    
    @Test
    public void retirarDineroTest6_SaldoInsuficiente() {
        String useremail = "usuario@test.com";
        float cantidad = 100; // Más de lo que tiene en el monedero
        User usuarioFalso = new User(useremail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        cuentaFalsa.setNumeroRandom(200);
        Monedero monederoExistente = new Monedero(useremail + "_wallet");
        monederoExistente.setSaldo(50.0f); // Solo tiene 50
        usuarioFalso.setMonedero(monederoExistente);
        
        Mockito.when(db.find(User.class, useremail)).thenReturn(usuarioFalso);
        
        try {
            sut.open();
            Monedero result = sut.retirarDinero(useremail, cantidad);
            sut.close();
            fail("Debería lanzar SaldoInsuficienteException");
        } catch (SaldoInsuficienteException e) {
            assertEquals("Saldo insuficiente en el monedero", e.getMessage());
            assertTrue("Saldo insuficiente detectado correctamente", true);
        } catch (Exception e) {
            fail("Lanzó excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

}