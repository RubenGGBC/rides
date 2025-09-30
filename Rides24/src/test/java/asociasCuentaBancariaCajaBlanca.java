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
        
    }

    @Test
    public void asociarCuentaBancariaTest2_MonederoNoExiste() {
        
    }

    @Test
    public void asociarCuentaBancariaTest3_CasoExitoso() {
        
    }

    @Test
    public void asociarCuentaBancariaTest4_UsuarioConCuentaExistente() {
        
    }

    @Test
    public void asociarCuentaBancariaTest5_CuentaNula() {
        
    }
}
