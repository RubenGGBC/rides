package gui;

import businessLogic.BLFacade;
import domain.Ride;
import domain.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class MisReservasGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTable tableRides;
    private DefaultTableModel tableModelRides;
    private JScrollPane scrollPaneRides;
    private JButton jButtonClose;
    private JLabel titleLabel;

    private String[] columnNamesRides = {
            "Salida", "Destino", "Conductor", "Plazas", "Precio", "Estado"
    };

    public MisReservasGUI(User user) {
        setTitle("Mis Reservas");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Título
        titleLabel = new JLabel("Mis Reservas de Viajes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(titleLabel, BorderLayout.NORTH);

        // Tabla
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
        scrollPaneRides.setBorder(BorderFactory.createTitledBorder("Reservas"));

        getContentPane().add(scrollPaneRides, BorderLayout.CENTER);

        // Cargar datos
        List<Ride> rides = user.getReservedRides();
        for (Ride ride : rides) {
            Vector<Object> row = new Vector<>();
            row.add(ride.getFrom());
            row.add(ride.getTo());
            row.add(ride.getDriver().getName());
            row.add(ride.getnPlaces());
            row.add(ride.getPrice());
            row.add(ride.getEstado());
            tableModelRides.addRow(row);
        }

        // Botón Cerrar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(Color.WHITE);

        jButtonClose = new JButton("Cerrar");
        jButtonClose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jButtonClose.setFocusPainted(false);
        jButtonClose.setBackground(new Color(244, 67, 54));
        jButtonClose.setForeground(Color.WHITE);
        jButtonClose.addActionListener(e -> dispose());

        buttonPanel.add(jButtonClose);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
}
