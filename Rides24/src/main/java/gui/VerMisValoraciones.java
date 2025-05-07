package gui;

import java.awt.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.Driver;
import domain.User;
import domain.Valoracion;

public class VerMisValoraciones extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTable tableValoraciones;
    private DefaultTableModel tableModelValoraciones;
    private JScrollPane scrollPaneValoraciones;
    private JButton jButtonClose;
    private JLabel titleLabel;

    private String[] columnNames = {
            "Estrellas", "Comentario", "Usuario que valoró"
    };

    public VerMisValoraciones(User user) {
        setTitle("Mis Valoraciones");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));

        // Paleta de colores
        Color backgroundColor = new Color(250, 250, 250); // fondo suave
        Color tableHeaderColor = new Color(100, 149, 237); // azul cornflower
        Color selectionColor = new Color(204, 228, 247); // celeste muy claro
        Color buttonColor = new Color(33, 150, 243); // azul medio
        Color buttonTextColor = Color.WHITE;

        getContentPane().setBackground(backgroundColor);

        // Título
        titleLabel = new JLabel("Comentarios Recibidos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 10));
        getContentPane().add(titleLabel, BorderLayout.NORTH);

        // Tabla
        tableModelValoraciones = new DefaultTableModel(null, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableValoraciones = new JTable(tableModelValoraciones);
        tableValoraciones.setRowHeight(26);
        tableValoraciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableValoraciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableValoraciones.getTableHeader().setBackground(tableHeaderColor);
        tableValoraciones.getTableHeader().setForeground(Color.WHITE);
        tableValoraciones.setSelectionBackground(selectionColor);
        tableValoraciones.setGridColor(new Color(230, 230, 230));
        tableValoraciones.setShowGrid(true);
        tableValoraciones.setShowVerticalLines(false);

        scrollPaneValoraciones = new JScrollPane(tableValoraciones);
        scrollPaneValoraciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Valoraciones recibidas"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        getContentPane().add(scrollPaneValoraciones, BorderLayout.CENTER);

        // Cargar datos
        BLFacade facade = MainGUI.getBusinessLogic();
        Driver d = facade.getDriverByUser(user);
        List<Valoracion> vals = facade.getValoraciones(d.getEmail());

        for (Valoracion val : vals) {
            Vector<Object> row = new Vector<>();
            row.add(val.getEstrellas());
            row.add(val.getComentario());
            row.add(val.getUsuario().getnombre());
            tableModelValoraciones.addRow(row);
        }

        // Botón Cerrar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(backgroundColor);

        jButtonClose = new JButton("Cerrar");
        jButtonClose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jButtonClose.setFocusPainted(false);
        jButtonClose.setBackground(buttonColor);
        jButtonClose.setForeground(buttonTextColor);
        jButtonClose.setPreferredSize(new Dimension(120, 35));
        jButtonClose.addActionListener(e -> dispose());

        buttonPanel.add(jButtonClose);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
}
