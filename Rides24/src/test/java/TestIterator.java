import businessLogic.BLFacade;
import businessLogic.ExtendedIterator;
import gui.BLFactory;

public class TestIterator {
    public static void main(String[] args) {
        boolean isLocal = true;
        BLFacade blFacade = new BLFactory().getBusinessLogicFactory(isLocal);
        ExtendedIterator<String> i = blFacade.getDepartingCitiesIterator();
        String c;
        System.out.println("_____________________");
        System.out.println("DEL ULTIMO AL PRIMERO");
        i.goLast();
        while (i.hasPrevious()) {
            c = i.previous();
            System.out.println(c);
        }
        System.out.println();
        System.out.println("_____________________");
        System.out.println("DEL PRIMERO AL ULTIMO");
        i.goFirst();
        while (i.hasNext()) {
            c = i.next();
            System.out.println(c);
        }
    }
}
