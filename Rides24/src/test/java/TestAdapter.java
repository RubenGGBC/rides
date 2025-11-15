import businessLogic.BLFacade;
import domain.Driver;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.UserAlredyExistException;
import gui.BLFactory;
import gui.DriverTable;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class TestAdapter {
    public static void main(String[] args) throws UserAlredyExistException, RideAlreadyExistException, RideMustBeLaterThanTodayException {
        boolean isLocal = true;
        BLFacade blFacade = new BLFactory().getBusinessLogicFactory(isLocal);

        Date testDate = new Date(126, 11, 12);
        Ride createdRide = blFacade.createRide("Lumiere", "Paris", testDate, 5, 50, "driver3@gmail.com");

        Driver d = blFacade.getDriver("driver3@gmail.com");
        DriverTable dt = new DriverTable(d);

        dt.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                d.removeRide("Lumiere", "Paris", testDate);
                blFacade.updatearDriver(d);
                System.out.println("Viaje de prueba eliminado de la BD");
                System.exit(0);
            }
        });

        dt.setVisible(true);
    }
}
