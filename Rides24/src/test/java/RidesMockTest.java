import businesslogic.blfacade;
import configuration.UtilDate;
import domain.Driver;
import gui.MainGUI;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.*;

public class RidesMockTest {
    static blfacade appFacadeInterface = Mockito.mock(blfacade.class);
    public static void main(String args[]) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        MainGUI sut = new MainGUI(new Driver("Jon", "jon@gmail.com"));
        MainGUI.setBussinessLogic(appFacadeInterface);
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        sut.setVisible(true);
        List<String> departingList = new ArrayList<String>(Arrays.asList("Bilbo","Donostia","Gasteiz"));
        List<String> arrivalList = new ArrayList<String>(Arrays.asList("Madrid","Barcelona"));
        Mockito.when(appFacadeInterface.getDepartCities()).thenReturn(departingList);
        Mockito.when(appFacadeInterface.getDestinationCities("Donostia")).thenReturn(arrivalList);
        List<Date> resultDates = new ArrayList<Date>();
        Calendar today = Calendar.getInstance();
        int month=today.get(Calendar.MONTH);
        int year=today.get(Calendar.YEAR);
        if (month==12) { month=1; year+=1;}
        resultDates.add(UtilDate.newDate(year,month, 23));
        resultDates.add(UtilDate.newDate(year,month, 26));
        Mockito.when(appFacadeInterface.getThisMonthDatesWithRides(Mockito.eq("Donostia"),Mockito.eq("Madrid"),Mockito.any(Date.class))).thenReturn(resultDates);

    } }