package gui;

import businesslogic.BLFacade;
import configuration.UtilDate;

import com.toedter.calendar.JCalendar;

import domain.EstadoViaje;
import domain.Ride;
import domain.User;
import exceptions.AnyRidesException;

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class SolicitarReservaGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    protected static final EstadoViaje PENDIENTE = EstadoViaje.PENDIENTE;

    private JComboBox<String> jComboBoxOrigin = new JComboBox<>();
    private DefaultComboBoxModel<String> originLocations = new DefaultComboBoxModel<>();

    private JComboBox<String> jComboBoxDestination = new JComboBox<>();
    private DefaultComboBoxModel<String> destinationCities = new DefaultComboBoxModel<>();

    private JLabel jLabelOrigin = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.LeavingFrom"));
    private JLabel jLabelDestination = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.GoingTo"));
    private JLabel jLabelEventDate = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.RideDate"));
    private JLabel jLabelEvents = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.Rides"));

    private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
    private JButton jButtonReserva = new JButton("Reservar");

    private JCalendar jCalendar1 = new JCalendar();
    private Calendar calendarAnt = null;
    private Calendar calendarAct = null;
    private JScrollPane scrollPaneEvents = new JScrollPane();

    private List<Date> datesWithRidesCurrentMonth = new Vector<>();
 
    private JTable tableRides = new JTable();
    private DefaultTableModel tableModelRides;
    private String[] columnNamesRides = new String[] {
        ResourceBundle.getBundle("Etiquetas").getString("FindRidesGUI.Driver"),
        ResourceBundle.getBundle("Etiquetas").getString("FindRidesGUI.NPlaces"),
        ResourceBundle.getBundle("Etiquetas").getString("FindRidesGUI.Price")
    };

    public SolicitarReservaGUI(User user) {
        setTitle(ResourceBundle.getBundle("Etiquetas").getString("FindRidesGUI.FindRides"));
        setSize(750, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        BLFacade facade = MainGUI.getBusinessLogic();
        for (String location : facade.getDepartCities()) {
            originLocations.addElement(location);
        }

        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Selecciona origen, destino y fecha"));
        panelFiltros.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFiltros.add(jLabelOrigin, gbc);

        gbc.gridx = 1;
        jComboBoxOrigin.setModel(originLocations);
        panelFiltros.add(jComboBoxOrigin, gbc);

        // 
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFiltros.add(jLabelDestination, gbc);

        gbc.gridx = 1;
        jComboBoxDestination.setModel(destinationCities);
        panelFiltros.add(jComboBoxDestination, gbc);

        // 
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        jCalendar1.setPreferredSize(new Dimension(225, 150));
        panelFiltros.add(jCalendar1, gbc);

        getContentPane().add(panelFiltros, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setBackground(Color.WHITE);

        jLabelEvents.setHorizontalAlignment(SwingConstants.CENTER);
        panelCentro.add(jLabelEvents, BorderLayout.NORTH);

        tableModelRides = new DefaultTableModel(null, columnNamesRides);
        tableModelRides.setColumnCount(4);
        tableRides.setModel(tableModelRides);
        tableRides.setRowHeight(24);
        tableRides.getColumnModel().getColumn(0).setPreferredWidth(150);
        tableRides.getColumnModel().getColumn(1).setPreferredWidth(50);
        tableRides.getColumnModel().getColumn(2).setPreferredWidth(50);
        tableRides.getColumnModel().removeColumn(tableRides.getColumnModel().getColumn(3));

        scrollPaneEvents.setViewportView(tableRides);
        panelCentro.add(scrollPaneEvents, BorderLayout.CENTER);

        getContentPane().add(panelCentro, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(Color.WHITE);
        panelInferior.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10));

        jButtonReserva.setPreferredSize(new Dimension(130, 30));
        jButtonClose.setPreferredSize(new Dimension(130, 30));

        panelInferior.add(jButtonReserva);
        panelInferior.add(jButtonClose);

        getContentPane().add(panelInferior, BorderLayout.SOUTH);

        jButtonClose.addActionListener(e -> setVisible(false));

        jComboBoxOrigin.addItemListener(e -> {
            destinationCities.removeAllElements();
            List<String> aCities = facade.getDestinationCities((String) jComboBoxOrigin.getSelectedItem());
            for (String aciti : aCities) {
                destinationCities.addElement(aciti);
            }
            tableModelRides.setRowCount(0);
        });

        jComboBoxDestination.addItemListener(e -> {
            paintDaysWithEvents(jCalendar1, datesWithRidesCurrentMonth, new Color(210, 228, 238));
            datesWithRidesCurrentMonth = facade.getThisMonthDatesWithRides(
                (String) jComboBoxOrigin.getSelectedItem(),
                (String) jComboBoxDestination.getSelectedItem(),
                jCalendar1.getDate()
            );
            paintDaysWithEvents(jCalendar1, datesWithRidesCurrentMonth, Color.CYAN);
        });

        jCalendar1.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertychangeevent) {
                if (propertychangeevent.getPropertyName().equals("calendar")) {
                    calendarAnt = (Calendar) propertychangeevent.getOldValue();
                    calendarAct = (Calendar) propertychangeevent.getNewValue();

                    int monthAnt = calendarAnt.get(Calendar.MONTH);
                    int monthAct = calendarAct.get(Calendar.MONTH);
                    if (monthAct != monthAnt) {
                        if (monthAct == monthAnt + 2) {
                            calendarAct.set(Calendar.MONTH, monthAnt + 1);
                            calendarAct.set(Calendar.DAY_OF_MONTH, 1);
                        }
                        jCalendar1.setCalendar(calendarAct);
                    }

                    try {
                        tableModelRides.setRowCount(0);
                        List<Ride> rides = facade.getRides(
                            (String) jComboBoxOrigin.getSelectedItem(),
                            (String) jComboBoxDestination.getSelectedItem(),
                            UtilDate.trim(jCalendar1.getDate())
                        );

                        DateFormat dateformat1 = DateFormat.getDateInstance(1, jCalendar1.getLocale());
                        if (rides.isEmpty()) {
                            jLabelEvents.setText(ResourceBundle.getBundle("Etiquetas").getString("FindRidesGUI.NoRides") + ": " + dateformat1.format(calendarAct.getTime()));
                        } else {
                            jLabelEvents.setText(ResourceBundle.getBundle("Etiquetas").getString("FindRidesGUI.Rides") + ": " + dateformat1.format(calendarAct.getTime()));
                        }

                        for (Ride ride : rides) {
                            Vector<Object> row = new Vector<>();
                            row.add(ride.getDriver().getName());
                            row.add(ride.getnPlaces());
                            row.add(ride.getPrice());
                            row.add(ride);
                            tableModelRides.addRow(row);
                        }

                        datesWithRidesCurrentMonth = facade.getThisMonthDatesWithRides(
                            (String) jComboBoxOrigin.getSelectedItem(),
                            (String) jComboBoxDestination.getSelectedItem(),
                            jCalendar1.getDate()
                        );
                        paintDaysWithEvents(jCalendar1, datesWithRidesCurrentMonth, Color.CYAN);

                        tableRides.getColumnModel().removeColumn(tableRides.getColumnModel().getColumn(3));
                    } catch (Exception e1) {
                        e1.printStackTrace(); 
                    }
                }
            }
        });

        jButtonReserva.addActionListener(e -> {
            int selectedRow = tableRides.getSelectedRow();
            if (selectedRow != -1 && tableRides.getRowCount() > 0) {
                Ride rideSeleccionada = (Ride) tableModelRides.getValueAt(selectedRow, 3);
                try {
                    if(user.getMonedero().getSaldo() < rideSeleccionada.getPrice()) {
                        JOptionPane.showMessageDialog(this, "No tienes suficiente saldo", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        rideSeleccionada.setEstado(PENDIENTE);
                        facade.reserva(rideSeleccionada);
                        facade.añadir(rideSeleccionada, user.getEmail());
                        facade.cobro(user.getMonedero(), rideSeleccionada.getPrice());
                        JOptionPane.showMessageDialog(this, "Reserva solicitada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (AnyRidesException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "No se pudo realizar la reserva.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un viaje para reservar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Cargar datos iniciales
        if (jComboBoxOrigin.getItemCount() > 0 && jComboBoxDestination.getItemCount() > 0) {
            datesWithRidesCurrentMonth = facade.getThisMonthDatesWithRides(
                (String) jComboBoxOrigin.getSelectedItem(),
                (String) jComboBoxDestination.getSelectedItem(),
                jCalendar1.getDate()
            );
            paintDaysWithEvents(jCalendar1, datesWithRidesCurrentMonth, Color.CYAN);
        }

        setVisible(true);
    }

    public static void paintDaysWithEvents(JCalendar jCalendar,List<Date> datesWithEventsCurrentMonth, Color color) {
		//		// For each day with events in current month, the background color for that day is changed to cyan.


		Calendar calendar = jCalendar.getCalendar();

		int month = calendar.get(Calendar.MONTH);
		int today=calendar.get(Calendar.DAY_OF_MONTH);
		int year=calendar.get(Calendar.YEAR);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int offset = calendar.get(Calendar.DAY_OF_WEEK);

		if (Locale.getDefault().equals(new Locale("es")))
			offset += 4;
		else
			offset += 5;


		for (Date d:datesWithEventsCurrentMonth){

			calendar.setTime(d);


			// Obtain the component of the day in the panel of the DayChooser of the
			// JCalendar.
			// The component is located after the decorator buttons of "Sun", "Mon",... or
			// "Lun", "Mar"...,
			// the empty days before day 1 of month, and all the days previous to each day.
			// That number of components is calculated with "offset" and is different in
			// English and Spanish
			//			    		  Component o=(Component) jCalendar.getDayChooser().getDayPanel().getComponent(i+offset);; 
			Component o = (Component) jCalendar.getDayChooser().getDayPanel()
					.getComponent(calendar.get(Calendar.DAY_OF_MONTH) + offset);
			o.setBackground(color);
		}

		calendar.set(Calendar.DAY_OF_MONTH, today);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);


	}
    	
}