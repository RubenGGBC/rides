package bussinessLogic;


import dataAccess.DBInterface;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class BL {
    public static DBInterface db = Mockito.mock(DBInterface.class);
    public static void main(String args[]) {
        MockitoAnnotations.initMocks(args);
        Mockito.doReturn("Hola").when(db).getUser("123");
        System.out.println(db.getUser("123"));
        System.out.println(db.getUser("456"));
    }
}
