package gui;

import javax.swing.*;

import businesslogic.BLFacade;
import domain.EstadoViaje;
import domain.Ride;
import domain.User;
import domain.Valoracion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class AddReviewGUI extends JFrame {
    private User user;
    private JComboBox<Ride> rideComboBox;
    private JSlider ratingSlider;
    private JTextArea reviewTextArea;
    private JButton submitButton;
    private ResourceBundle resourceBundle;

    public AddReviewGUI(User user) {
        this.user = user;
        
        try {
            Locale locale = Locale.getDefault(); 
            resourceBundle = ResourceBundle.getBundle("Etiquetas", locale);
        } catch (Exception e) {
            System.out.println("Error loading resource bundle: " + e.getMessage());
            resourceBundle = ResourceBundle.getBundle("Etiquetas");
        }

        setTitle(resourceBundle.getString("AddReview.Title"));
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png")); 
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println(resourceBundle.getString("AddReview.IconNotFound"));
        }

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(contentPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(resourceBundle.getString("AddReview.Title"));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(33, 47, 61));
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Rides
        JLabel rideLabel = new JLabel(resourceBundle.getString("AddReview.SelectRide"));
        rideLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPanel.add(rideLabel);

        BLFacade facade = MainGUI.getBusinessLogic();
        List<Ride> rides = user.getReservedRides();
        List<Ride> confirmedRides = new ArrayList<>();
        for (Ride r : rides) {
            if (r.getEstado() == EstadoViaje.CONFIRMADO) {
                confirmedRides.add(r);
            }
        }
        rideComboBox = new JComboBox<>(confirmedRides.toArray(new Ride[0]));
        rideComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(rideComboBox);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Rating
        JLabel ratingLabel = new JLabel(resourceBundle.getString("AddReview.Rating"));
        ratingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPanel.add(ratingLabel);

        ratingSlider = new JSlider(1, 5, 3);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setBackground(Color.WHITE);
        contentPanel.add(ratingSlider);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Comment
        JLabel commentLabel = new JLabel(resourceBundle.getString("AddReview.WriteReview"));
        commentLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPanel.add(commentLabel);

        reviewTextArea = new JTextArea(4, 20);
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        reviewTextArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Button
        submitButton = new JButton(resourceBundle.getString("AddReview.Submit"));
        submitButton.setBackground(new Color(60, 179, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setPreferredSize(new Dimension(200, 40));

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ride selectedRide = (Ride) rideComboBox.getSelectedItem();
                int rating = ratingSlider.getValue();
                String review = reviewTextArea.getText();

                if (selectedRide != null) {
                    Valoracion val = new Valoracion(rating, review, user, selectedRide.getDriver());
                    facade.addValoracion(val);
                    JOptionPane.showMessageDialog(null, resourceBundle.getString("AddReview.Success"));
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, resourceBundle.getString("AddReview.Error"), 
                            resourceBundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        contentPanel.add(submitButton);
    }
}    