import dataAccess.DataAccess;
import domain.CuentaBancaria;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.NonexitstenUserException;
import exceptions.SaldoInsuficienteException;
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
    public void test1() {
        // Test case 1: Cantidad negativa
        String userEmail = "userf@falso.com";
        float cantidad = -2;
        
        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar CantidadInvalidaException");
            
        } catch (CantidadInvalidaException e) {
            assertEquals("La cantidad a retirar debe ser mayor que cero", e.getMessage());
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test2() {
        // Test case 2: Usuario no existe
        String userEmail = "rgallego007@ikasle.ehu.eus";
        float cantidad = 50.0f;
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(null);
        
        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar NonexitstenUserException");
            
        } catch (NonexitstenUserException e) {
            assertTrue("El usuario no existe", true);
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test3() {
        // Test case 3: Usuario sin monedero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;
        
        User usuarioFalso = new User(userEmail, pass, false, userName);
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(30);
        // Sin monedero
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar MonederoNoExisteException");
            
        } catch (MonederoNoExisteException e) {
            assertEquals("El usuario no tiene monedero", e.getMessage());
        } catch (SaldoInsuficienteException e){
            assertTrue("No hay dinero en la cuenta",true);
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }





    @Test
    public void test4() {
        // Test case 4: Usuario con monedero pero saldo insuficiente
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;
        
        User usuarioFalso = new User(userEmail, pass, false, userName);
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(30);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(20.0f); // Saldo insuficiente
        usuarioFalso.setMonedero(monedero);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            sut.retirarDinero(userEmail, cantidad);
            fail("Debería lanzar SaldoInsuficienteException");
            
        } catch (SaldoInsuficienteException e) {
            assertEquals("Saldo insuficiente en el monedero", e.getMessage());
            assertNotNull("El usuario debería existir", usuarioFalso);
            assertNotNull("El monedero debería existir", usuarioFalso.getMonedero());
            
        } catch (Exception e) {
            fail("Excepción incorrecta: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test5() {
        // Test case 5: Caso exitoso - retiro de dinero
        String userEmail = "rgallego007@ikasle.ehu.eus";
        String pass = "contraseña";
        String userName = "UserTest";
        float cantidad = 50.0f;
        
        User usuarioFalso = new User(userEmail, pass, false, userName);
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(50.0f); // Saldo exacto
        usuarioFalso.setMonedero(monedero);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            
            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe coincidir", 0.0f, result.getSaldo(), 0.01);
            assertEquals("El saldo de cuenta debe haberse incrementado", 150.0f, usuarioFalso.getCuenta().getNumeroRandom(), 0.01);
            
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test6_ValorLimiteMinimo() {
        // Test case 6: Valor límite mínimo - cantidad = 0.01
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = 0.01f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(150.0f);
        usuarioFalso.setMonedero(monedero);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            
            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe haberse reducido", 149.99f, result.getSaldo(), 0.001);
            assertEquals("El saldo de cuenta debe haberse incrementado", 100.01f, usuarioFalso.getCuenta().getNumeroRandom(), 1.0f);
            
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void test7_ValorLimiteMaximo() {
        // Test case 7: Valor límite máximo - cantidad = 149.99
        String userEmail = "mberasategui022@ikasle.ehu.eus";
        float cantidad = 149.99f;
        
        User usuarioFalso = new User(userEmail, "contraseña", false, "UserTest");
        CuentaBancaria cuentaFalsa = new CuentaBancaria("1234567890");
        usuarioFalso.setCuenta(cuentaFalsa);
        usuarioFalso.getCuenta().setNumeroRandom(100);
        Monedero monedero = new Monedero(userEmail + "_wallet");
        monedero.setSaldo(150.0f);
        usuarioFalso.setMonedero(monedero);
        
        Mockito.when(db.find(User.class, userEmail)).thenReturn(usuarioFalso);
        
        try {
            Monedero result = sut.retirarDinero(userEmail, cantidad);
            
            assertNotNull("El monedero debería existir", result);
            assertEquals("El saldo del monedero debe haberse reducido", 0.01f, result.getSaldo(), 0.001);
            assertEquals("El saldo de cuenta debe haberse incrementado", 249.99f, usuarioFalso.getCuenta().getNumeroRandom(), 1.0f);
            
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

}