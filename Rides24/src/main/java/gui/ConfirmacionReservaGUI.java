package gui;

import businessLogic.BLFacade;
import domain.Driver;
import domain.EstadoViaje;
import domain.Ride;
import domain.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class ConfirmacionReservaGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JTable tableRides = new JTable();
    private DefaultTableModel tableModelRides;
    private JScrollPane scrollPaneRides = new JScrollPane();
    private JButton jButtonClose = new JButton("Cerrar");
    
    private String[] columnNamesRides = new String[] {
            "Salida", "Destino", "Plazas", "Precio", "Estado"
    };

    public ConfirmacionReservaGUI(User user) {
        getContentPane().setLayout(null);
        this.setSize(new Dimension(500, 400));
        this.setTitle("Solicitudes de reserva");
        
        jButtonClose.setBounds(new Rectangle(266, 320, 130, 30));
        jButtonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        tableModelRides = new DefaultTableModel(null, columnNamesRides);
        tableRides.setModel(tableModelRides);
        
        BLFacade facade = MainGUI.getBusinessLogic();
        Driver d = facade.getDriverByUser(user);
        List<Ride> rides = d.getRides();
        
        for (Ride ride : rides) {
            Vector<Object> row = new Vector<>();
            row.add(ride.getFrom());
            row.add(ride.getTo());
            row.add(ride.getnPlaces());
            row.add(ride.getPrice());
            row.add(ride.getEstado());
            tableModelRides.addRow(row);
        }
        
        scrollPaneRides.setBounds(new Rectangle(22, 50, 452, 250));
        scrollPaneRides.setViewportView(tableRides);
        
        getContentPane().add(scrollPaneRides);
        getContentPane().add(jButtonClose);
 
        JButton btnConfirmarReserva = new JButton("Confirmar Reserva");
        btnConfirmarReserva.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableRides.getSelectedRow();
                
                if (selectedRow != -1) {
                    Ride selectedRide = rides.get(selectedRow);
                    if(selectedRide.getEstado()==EstadoViaje.PENDIENTE) {
                        selectedRide.setEstado(EstadoViaje.CONFIRMADO);
                        facade.updateRide(selectedRide);
                        tableModelRides.setValueAt(EstadoViaje.CONFIRMADO, selectedRow, 4);
                        JOptionPane.showMessageDialog(null, "Reserva confirmada correctamente.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona un viaje antes de confirmar o selecciona un viaje pendiente");
                }
            }
        });
        btnConfirmarReserva.setBounds(35, 320, 167, 27);
        getContentPane().add(btnConfirmarReserva);
    }
}
