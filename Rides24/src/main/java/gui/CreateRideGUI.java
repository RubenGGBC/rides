package gui;

import java.text.DateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;

import com.toedter.calendar.JCalendar;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import businesslogic.BLFacade;
import configuration.UtilDate;
import domain.Driver;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

public class CreateRideGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    // Custom colors to match the screenshot
    private final Color BLUE_HEADER = new Color(70, 130, 180);
    private final Color BLUE_BUTTON = new Color(70, 130, 180);
    private final Color WHITE_TEXT = Color.WHITE;
    
    private Driver driver;
    private JTextField fieldOrigin = new JTextField();
    private JTextField fieldDestination = new JTextField();
    
    private JLabel jLabelOrigin = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.LeavingFrom"));
    private JLabel jLabelDestination = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.GoingTo")); 
    private JLabel jLabelSeats = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.NumberOfSeats"));
    private JLabel jLabRideDate = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.RideDate"));
    private JLabel jLabelPrice = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.Price"));
    private JLabel jLabelHeader = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.CreateRide"));

    private JTextField jTextFieldSeats = new JTextField();
    private JTextField jTextFieldPrice = new JTextField();

    private JCalendar jCalendar = new JCalendar(); 
    private Calendar calendarAct = null;
    private Calendar calendarAnt = null;

    private JScrollPane scrollPaneEvents = new JScrollPane();

    private JButton jButtonCreate = new JButton(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.CreateRide"));
    private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
    private JLabel jLabelMsg = new JLabel();
    private JLabel jLabelError = new JLabel();
    
    private List<Date> datesWithEventsCurrentMonth;
    
    // Header panel
    private JPanel headerPanel;

    public CreateRideGUI(Driver driver) {
        this.driver = driver;
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(700, 500));
        this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.CreateRide"));
        
        // Create header panel
        headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, 700, 40);
        headerPanel.setBackground(BLUE_HEADER);
        
        // Style header label
        jLabelHeader.setBounds(20, 5, 200, 30);
        jLabelHeader.setForeground(WHITE_TEXT);
        jLabelHeader.setFont(new Font("Dialog", Font.BOLD, 18));
        headerPanel.add(jLabelHeader);
        
        // Add header panel to frame
        this.getContentPane().add(headerPanel);

        // Layout components with a starting y-position that leaves room for the header
        int startY = 60;

        jLabelOrigin.setBounds(new Rectangle(30, startY, 120, 25));
        jLabelOrigin.setHorizontalAlignment(SwingConstants.RIGHT);
        
        fieldOrigin.setBounds(new Rectangle(160, startY, 200, 25));
        
        jLabelDestination.setBounds(new Rectangle(30, startY + 35, 120, 25));
        jLabelDestination.setHorizontalAlignment(SwingConstants.RIGHT);
        
        fieldDestination.setBounds(new Rectangle(160, startY + 35, 200, 25));
        
        jLabelSeats.setBounds(new Rectangle(30, startY + 70, 120, 25));
        jLabelSeats.setHorizontalAlignment(SwingConstants.RIGHT);
        
        jTextFieldSeats.setBounds(new Rectangle(160, startY + 70, 200, 25));
        
        jLabelPrice.setBounds(new Rectangle(30, startY + 105, 120, 25));
        jLabelPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        
        jTextFieldPrice.setBounds(new Rectangle(160, startY + 105, 200, 25));
        
        jLabRideDate.setBounds(new Rectangle(400, startY, 200, 25));
        
        jCalendar.setBounds(new Rectangle(400, startY + 30, 250, 200));
        
        jLabelMsg.setBounds(new Rectangle(30, startY + 150, 330, 25));
        jLabelMsg.setForeground(Color.red);

        jLabelError.setBounds(new Rectangle(30, startY + 180, 330, 25));
        jLabelError.setForeground(Color.red);

        // Style buttons to match the blue theme
        jButtonCreate.setBounds(new Rectangle(160, startY + 220, 150, 35));
        jButtonCreate.setBackground(BLUE_BUTTON);
        jButtonCreate.setForeground(WHITE_TEXT);
        jButtonCreate.setFocusPainted(false);
        
        jButtonClose.setBounds(new Rectangle(320, startY + 220, 150, 35));
        jButtonClose.setBackground(BLUE_BUTTON);
        jButtonClose.setForeground(WHITE_TEXT);
        jButtonClose.setFocusPainted(false);

        jButtonCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButtonCreate_actionPerformed(e);
            }
        });
        
        jButtonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButtonClose_actionPerformed(e);
            }
        });

        // Add all components to the content pane
        this.getContentPane().add(jLabelOrigin);
        this.getContentPane().add(fieldOrigin);
        this.getContentPane().add(jLabelDestination);
        this.getContentPane().add(fieldDestination);
        this.getContentPane().add(jLabelSeats);
        this.getContentPane().add(jTextFieldSeats);
        this.getContentPane().add(jLabelPrice);
        this.getContentPane().add(jTextFieldPrice);
        this.getContentPane().add(jLabRideDate);
        this.getContentPane().add(jCalendar);
        this.getContentPane().add(jLabelMsg);
        this.getContentPane().add(jLabelError);
        this.getContentPane().add(jButtonCreate);
        this.getContentPane().add(jButtonClose);

        BLFacade facade = MainGUI.getBusinessLogic();
        datesWithEventsCurrentMonth = facade.getThisMonthDatesWithRides("a", "b", jCalendar.getDate());

        // Code for JCalendar
        this.jCalendar.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertychangeevent) {
                if (propertychangeevent.getPropertyName().equals("locale")) {
                    jCalendar.setLocale((Locale) propertychangeevent.getNewValue());
                } else if (propertychangeevent.getPropertyName().equals("calendar")) {
                    calendarAnt = (Calendar) propertychangeevent.getOldValue();
                    calendarAct = (Calendar) propertychangeevent.getNewValue();
                    DateFormat dateformat1 = DateFormat.getDateInstance(1, jCalendar.getLocale());
                    
                    int monthAnt = calendarAnt.get(Calendar.MONTH);
                    int monthAct = calendarAct.get(Calendar.MONTH);
                    if (monthAct != monthAnt) {
                        if (monthAct == monthAnt + 2) { 
                            // Si en JCalendar está 30 de enero y se avanza al mes siguiente, devolverá 2 de marzo (se toma como equivalente a 30 de febrero)
                            // Con este código se dejará como 1 de febrero en el JCalendar
                            calendarAct.set(Calendar.MONTH, monthAnt + 1);
                            calendarAct.set(Calendar.DAY_OF_MONTH, 1);
                        }
                        
                        jCalendar.setCalendar(calendarAct);
                    }
                    
                    jCalendar.setCalendar(calendarAct);
                    int offset = jCalendar.getCalendar().get(Calendar.DAY_OF_WEEK);
                    
                    if (Locale.getDefault().equals(new Locale("es")))
                        offset += 4;
                    else
                        offset += 5;
                        
                    Component o = (Component) jCalendar.getDayChooser().getDayPanel().getComponent(jCalendar.getCalendar().get(Calendar.DAY_OF_MONTH) + offset);
                }
            }
        });
        
        // Set window appearance
        this.setResizable(false);
        this.getContentPane().setBackground(Color.WHITE);
    }    
    
    private void jButtonCreate_actionPerformed(ActionEvent e) {
        jLabelMsg.setText("");
        String error = field_Errors();
        if (error != null) 
            jLabelMsg.setText(error);
        else
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                int inputSeats = Integer.parseInt(jTextFieldSeats.getText());
                float price = Float.parseFloat(jTextFieldPrice.getText());

                Ride r = facade.createRide(fieldOrigin.getText(), fieldDestination.getText(), 
                                         UtilDate.trim(jCalendar.getDate()), inputSeats, price, driver.getEmail());
                jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.RideCreated"));
            //error sonar ruben arreglado A,L
            } catch (RideMustBeLaterThanTodayException | RideAlreadyExistException e1) {
                jLabelMsg.setText(e1.getMessage());
            }
    }

    private void jButtonClose_actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }
    
    private String field_Errors() {
        try {
            if ((fieldOrigin.getText().length() == 0) || (fieldDestination.getText().length() == 0) || 
                (jTextFieldSeats.getText().length() == 0) || (jTextFieldPrice.getText().length() == 0))
                return ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.ErrorQuery");
            else {
                // trigger an exception if the introduced string is not a number
                int inputSeats = Integer.parseInt(jTextFieldSeats.getText());

                if (inputSeats <= 0) {
                    return ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.SeatsMustBeGreaterThan0");
                }
                else {
                    float price = Float.parseFloat(jTextFieldPrice.getText());
                    if (price <= 0) 
                        return ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.PriceMustBeGreaterThan0");
                    else 
                        return null;
                }
            }
        } catch (java.lang.NumberFormatException e1) {
            return ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.ErrorNumber");        
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }
}
