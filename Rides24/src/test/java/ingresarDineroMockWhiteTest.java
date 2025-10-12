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

public class ingresarDineroMockWhiteTest {
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
        System.out.println("Fin de la prueba");


    }



    // COMENTADO: Path mas sencillo - Usuario no existe
//    @Test
//    public void ingresarDineroTest1() throws NonexitstenUserException, CantidadInvalidaException {
//        String userEmail = "rgallego007@ikasle.ehu.eus";
//        float cantidad = 50.0f;
//
//        // Camino 1: usuario no esta en la db
//        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
//
//        try {
//            sut.open();
//            try {
//                sut.ingresarDinero(userEmail, cantidad);
//            } catch (MonederoNoExisteException e) {
//                throw new RuntimeException(e);
//            }
//            fail("Deberia lanzar NonexitstenUserException");
//        } catch (NonexitstenUserException e) {
//            assertTrue("El usuario no existe", true);
//        } finally {
//            sut.close();
//        }
//    }


    /*
    @Test
    public void ingresarDineroCajaTest2() throws NonexitstenUserException, MonederoNoExisteException, CantidadInvalidaException {
        String useremail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50;
        User usuarioFalso = new User(useremail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(30);
        Monedero monedero=new Monedero();
        usuarioFalso.setMonedero(monedero);
        monedero.setUser(usuarioFalso);//La cuenta tiene 30 euros
        Mockito.when(db.find(User.class, useremail)).thenReturn(usuarioFalso);
        try {
            sut.open();
            Monedero monederoresult = sut.ingresarDinero(useremail, cantidad);
            sut.close();
            fail("Si llega aqui no ha saltado la excepcion");


        } catch (MonederoNoExisteException e) {
            fail("Si llega aqui no ha saltado la excepcion");
        } catch (NonexitstenUserException e) {
            fail("Si llega aqui no ha saltado la excepcion");
        } catch (CantidadInvalidaException e) {
            assertTrue(true);
        }

    }
    */

    @Test
    public void ingresarDinderoTest3_UsuarioSinMonedero_SaldoInsuficiente() {
        String useremail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50;

        User usuarioFalso = new User(useremail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        cuentaFalsa.setNumeroRandom(30);
        usuarioFalso.setCuenta(cuentaFalsa);

        Mockito.when(db.find(User.class, useremail)).thenReturn(usuarioFalso);

        try {
            sut.open();
            Monedero result = sut.ingresarDinero(useremail, cantidad);
            fail("Deberia lanzar CantidadInvalidaException");
        } catch (CantidadInvalidaException e) {
            assertEquals("No tienes tanto dinero en la cuenta", e.getMessage());
                assertNotNull(usuarioFalso.getMonedero());
            assertEquals(0.0f, usuarioFalso.getMonedero().getSaldo(), 0.01f);
        } catch (Exception e) {
            fail("Lanzo excepcion incorrecta: " + e.getClass().getSimpleName());
        } finally {
            sut.close();
        }
    }

    @Test
    public void ingresarDineroTest4_CrearMonederoAutomaticoDineroencuenta() {
        String useremail = "nuevo@usuario.com";
        float cantidad = 30;
        User usuarioFalso = new User(useremail, "userfalso", false, "UserF");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        cuentaFalsa.setNumeroRandom(100);
        usuarioFalso.setMonedero(null);

        Mockito.when(db.find(User.class, useremail)).thenReturn(usuarioFalso);

        try {
            sut.open();
            Monedero result = sut.ingresarDinero(useremail, cantidad);
            sut.close();

            assertNotNull("Monedero se creó automáticamente", usuarioFalso.getMonedero());
            assertEquals(30.0f, result.getSaldo(), 0.01f);
            assertEquals(70, usuarioFalso.getCuenta().getNumeroRandom());

        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getClass().getSimpleName());
        }
    }


}