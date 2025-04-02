package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import domain.Driver;
import domain.EstadoViaje;
import domain.Ride;
import domain.User;
import domain.Valoracion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AddReviewGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<Ride> rideComboBox;
    private JSlider ratingSlider;
    private JTextArea reviewTextArea;
    private JButton submitButton;
    private User user;

    public AddReviewGUI(User user) {
        this.user = user;
        setTitle("Añadir Valoración");
        setSize(400, 300);
        setLayout(new GridLayout(4, 1));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        BLFacade facade = MainGUI.getBusinessLogic();
        List<Ride> rides = user.getReservedRides();
        List<Ride> conf = new ArrayList<Ride>();
        for(int i =0;i<rides.size();i++) {
        	if(rides.get(i).getEstado()==EstadoViaje.CONFIRMADO) {
        		conf.add(rides.get(i));
        	}
        }
        rideComboBox = new JComboBox<>(conf.toArray(new Ride[0]));
        ratingSlider = new JSlider(1, 5, 3);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        reviewTextArea = new JTextArea("Escribe tu reseña aquí...");
        submitButton = new JButton("Enviar");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ride selectedRide = (Ride) rideComboBox.getSelectedItem();
                int rating = ratingSlider.getValue();
                String review = reviewTextArea.getText();

                if (selectedRide != null) {
                	Valoracion val= new Valoracion(rating,review,user,selectedRide.getDriver());
                    facade.addValoracion(val);
                    JOptionPane.showMessageDialog(null, "Valoración añadida correctamente.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona un viaje válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        add(new JLabel("Selecciona un viaje:"));
        add(rideComboBox);
        add(new JLabel("Calificación (1-5):"));
        add(ratingSlider);
        add(new JLabel("Reseña:"));
        add(new JScrollPane(reviewTextArea));
        add(submitButton);
    }
}
