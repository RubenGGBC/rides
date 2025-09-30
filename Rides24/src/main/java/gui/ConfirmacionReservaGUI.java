package gui;

import businesslogic.BLFacade;
import domain.Driver;
import domain.EstadoViaje;
import domain.Ride;
import domain.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

public class ConfirmacionReservaGUI extends JFrame {
    private JLabel titleLabel;
    private JTable tableRides;
    private DefaultTableModel tableModelRides;
    private JScrollPane scrollPaneRides;
    private JButton btnConfirmarReserva;
    private JButton jButtonClose;
    private ResourceBundle resourceBundle;

    private String[] columnNamesRides;

    public ConfirmacionReservaGUI(User user) {
        try {
            Locale locale = Locale.getDefault(); 
            resourceBundle = ResourceBundle.getBundle("Etiquetas", locale);
        } catch (Exception e) {
            System.out.println("Error loading resource bundle: " + e.getMessage());
            resourceBundle = ResourceBundle.getBundle("Etiquetas");
        }

        // Initialize column names from resource bundle
        columnNamesRides = new String[] {
            resourceBundle.getString("ConfirmReservation.Departure"),
            resourceBundle.getString("ConfirmReservation.Destination"),
            resourceBundle.getString("ConfirmReservation.Seats"),
            resourceBundle.getString("ConfirmReservation.Price"),
            resourceBundle.getString("ConfirmReservation.Status")
        };

        setTitle(resourceBundle.getString("ConfirmReservation.Title"));
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        titleLabel = new JLabel(resourceBundle.getString("ConfirmReservation.Header"), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(titleLabel, BorderLayout.NORTH);

        tableModelRides = new DefaultTableModel(null, columnNamesRides) {
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tableRides = new JTable(tableModelRides);
        tableRides.setRowHeight(25);
        tableRides.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableRides.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableRides.setSelectionBackground(new Color(184, 207, 229));
        scrollPaneRides = new JScrollPane(tableRides);
        scrollPaneRides.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("ConfirmReservation.Rides")));

        getContentPane().add(scrollPaneRides, BorderLayout.CENTER);

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
            row.add(ride.getPrice());
            tableModelRides.addRow(row);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnConfirmarReserva = new JButton(resourceBundle.getString("ConfirmReservation.ConfirmButton"));
        btnConfirmarReserva.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnConfirmarReserva.setFocusPainted(false);
        btnConfirmarReserva.setBackground(new Color(76, 175, 80));
        btnConfirmarReserva.setForeground(Color.WHITE);

        btnConfirmarReserva.addActionListener((ActionEvent e) -> {
            int selectedRow = tableRides.getSelectedRow();
            if (selectedRow != -1) {
                Ride selectedRide = rides.get(selectedRow);
                if (selectedRide.getEstado() == EstadoViaje.PENDIENTE) {
                    selectedRide.setEstado(EstadoViaje.CONFIRMADO);
                    facade.updateRide(selectedRide);
                    facade.cobro(user.getMonedero(), -selectedRide.getPrice());
                    
                    
                    tableModelRides.setValueAt(EstadoViaje.CONFIRMADO, selectedRow, 4);
                    JOptionPane.showMessageDialog(this, resourceBundle.getString("ConfirmReservation.Success"));
                } else {
                    JOptionPane.showMessageDialog(this, resourceBundle.getString("ConfirmReservation.AlreadyConfirmed"));
                }
            } else {
                JOptionPane.showMessageDialog(this, resourceBundle.getString("ConfirmReservation.SelectRide"));
            }
        });

        jButtonClose = new JButton(resourceBundle.getString("ConfirmReservation.CloseButton"));
        jButtonClose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jButtonClose.setFocusPainted(false);
        jButtonClose.setBackground(new Color(244, 67, 54));
        jButtonClose.setForeground(Color.WHITE);
        jButtonClose.addActionListener(e -> dispose());

        buttonPanel.add(btnConfirmarReserva);
        buttonPanel.add(jButtonClose);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
}