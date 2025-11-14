package gui;

import domain.Driver;
import domain.Ride;
import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;

public class DriverAdapter extends AbstractTableModel {
    private Driver driver;
    private String[] columnNames = {"from", "to", "date", "places", "price"};

    public DriverAdapter(Driver driver) {
        this.driver = driver;
    }

    @Override
    public int getRowCount() {
        return driver.getRides().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ride ride = driver.getRides().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return ride.getFrom();
            case 1:
                return ride.getTo();
            case 2:
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                return dateFormat.format(ride.getDate());
            case 3:
                return ride.getnPlaces();
            case 4:
                return ride.getPrice();
            default:
                return null;
        }
    }
}
