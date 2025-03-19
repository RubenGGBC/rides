package gui;

import businessLogic.BLFacade;
import domain.Ride;
import domain.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class MisReservasGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JTable tableRides = new JTable();
    private DefaultTableModel tableModelRides;
    private JScrollPane scrollPaneRides = new JScrollPane();
    private JButton jButtonClose = new JButton("Cerrar");
    
    private String[] columnNamesRides = new String[] {
            "Conductor", "Plazas", "Precio"
    };

    public MisReservasGUI(User user) {
        this.setLayout(null);
        this.setSize(new Dimension(500, 400));
        this.setTitle("Mis Reservas");
        
        jButtonClose.setBounds(new Rectangle(180, 320, 130, 30));
        jButtonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        tableModelRides = new DefaultTableModel(null, columnNamesRides);
        tableRides.setModel(tableModelRides);
        
        List<Ride> rides = user.getReservedRides();
        for (Ride ride : rides) {
            Vector<Object> row = new Vector<>();
            row.add(ride.getDriver().getName());
            row.add(ride.getnPlaces());
            row.add(ride.getPrice());
            tableModelRides.addRow(row);
        }
        
        scrollPaneRides.setBounds(new Rectangle(50, 50, 400, 250));
        scrollPaneRides.setViewportView(tableRides);
        
        this.add(scrollPaneRides);
        this.add(jButtonClose);
    }
}
