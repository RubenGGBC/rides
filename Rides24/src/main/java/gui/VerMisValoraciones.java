package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Ride;
import domain.User;
import domain.Valoracion;

public class VerMisValoraciones extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    
    private JTable tableRides = new JTable();
    private DefaultTableModel tableModelRides;
    private JScrollPane scrollPaneRides = new JScrollPane();
    private JButton jButtonClose = new JButton("Cerrar");
    
    private String[] columnNamesRides = new String[] {
            "Estrellas","Comentarios","Valorador"
    };

    public VerMisValoraciones(User user) {
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
        BLFacade facade = MainGUI.getBusinessLogic();
        Driver d= facade.getDriverByUser(user);
        List<Valoracion> vals = facade.getValoraciones(d.getEmail());
        List<Ride> rides = user.getReservedRides();
        for (Valoracion val : vals) {
            Vector<Object> row = new Vector<>();
            row.add(val.getEstrellas());
            row.add(val.getComentario());
            row.add(val.getUsuario().getnombre());
           
            tableModelRides.addRow(row);
        }
        
        scrollPaneRides.setBounds(new Rectangle(50, 50, 400, 250));
        scrollPaneRides.setViewportView(tableRides);
        
        this.add(scrollPaneRides);
        this.add(jButtonClose);
    }
}
