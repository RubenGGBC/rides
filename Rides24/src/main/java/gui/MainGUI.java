package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import domain.Driver;
import domain.User;
import exceptions.UserAlredyExistException;
import exceptions.NonexitstenUserException;
import businessLogic.BLFacade;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainGUI extends JFrame {
    
    private Driver driver;
    private static final long serialVersionUID = 1L;
    private static BLFacade appFacadeInterface;
    
    // Colors - Enhanced Minimal palette
    private Color primaryColor = new Color(70, 130, 180); // Steel Blue
    private Color buttonHoverColor = new Color(100, 149, 237); // Cornflower Blue
    private Color backgroundColor = Color.WHITE;
    private Color textColor = new Color(51, 51, 51); // Dark gray
    private Color successColor = new Color(46, 139, 87); // Sea Green
    private Color errorColor = new Color(220, 20, 60); // Crimson
    
    // Improved fonts
    private Font basicFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 14);
     
    private JPanel contentPane;
    protected JLabel statusLabel;
    
    // Language selection
    private JRadioButton rdbtnEnglish;
    private JRadioButton rdbtnEuskara;
    private JRadioButton rdbtnCastellano;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    
    // Login fields
    private JTextField userTextField;
    private JTextField nameTextField;
    private JPasswordField passwordField;
    private JRadioButton btnDriver;
    private JRadioButton btnUser;
    private final ButtonGroup userTypeGroup = new ButtonGroup();
    
    // Added label fields as class variables so they can be accessed in paintAgain()
    private JLabel lblEmail;
    private JLabel lblPswd;
    private JLabel lblName;
    private JLabel lblType;
    private JButton btnLogin;
    private JButton btnRegister;
    
    // Operation buttons
    private JButton btnCreateRide;
    private JButton btnQueryRides;
    private JButton btnSolicitarReserva;
    private JButton btnVerReservas;
    private JButton btnSolicitudesReserva;
    private JButton btnValorarConductor;
    private JButton btnMisValoraciones;
    private JButton btnAñadirSaldo;
    
    public static BLFacade getBusinessLogic() {
        return appFacadeInterface;
    }
     
    public static void setBussinessLogic(BLFacade afi) {
        appFacadeInterface = afi;
    }
    
    /**
     * This is the default constructor
     */
    public MainGUI(Driver d) {
        super();
        driver = d;
        
        this.setSize(850, 550);
        this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + " - " + driver.getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Center on screen
        
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBackground(backgroundColor);
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
        
        // ========== HEADER PANEL ==========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        JLabel titleLabel = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle"));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Language panel
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.setOpaque(false);
        
        rdbtnEuskara = new JRadioButton("Euskara");
        rdbtnEuskara.setForeground(Color.WHITE);
        rdbtnEuskara.setOpaque(false);
        rdbtnEuskara.setFocusPainted(false);
        rdbtnEuskara.addActionListener(e -> {
            Locale.setDefault(new Locale("eus"));
            paintAgain();
        });
        buttonGroup.add(rdbtnEuskara);
        
        rdbtnCastellano = new JRadioButton("Castellano");
        rdbtnCastellano.setForeground(Color.WHITE);
        rdbtnCastellano.setOpaque(false);
        rdbtnCastellano.setFocusPainted(false);
        rdbtnCastellano.addActionListener(e -> {
            Locale.setDefault(new Locale("es"));
            paintAgain();
        });
        buttonGroup.add(rdbtnCastellano);
        rdbtnCastellano.setSelected(true);
        
        rdbtnEnglish = new JRadioButton("English");
        rdbtnEnglish.setForeground(Color.WHITE);
        rdbtnEnglish.setOpaque(false);
        rdbtnEnglish.setFocusPainted(false);
        rdbtnEnglish.addActionListener(e -> {
            Locale.setDefault(new Locale("en"));
            paintAgain();
        });
        buttonGroup.add(rdbtnEnglish);
        
        langPanel.add(rdbtnEuskara);
        langPanel.add(rdbtnCastellano);
        langPanel.add(rdbtnEnglish);
        
        headerPanel.add(langPanel, BorderLayout.EAST);
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // ========== CENTER PANEL ==========
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(backgroundColor);
        
        // Status message
        statusLabel = new JLabel(""); 
        statusLabel.setFont(basicFont);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // ========== OPERATIONS PANEL ==========
        JPanel operationsPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        operationsPanel.setBackground(backgroundColor);
        operationsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            "Operaciones",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            headerFont,
            textColor
        ));
        
        btnCreateRide = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateRide"));
        styleButton(btnCreateRide);
        btnCreateRide.addActionListener(e -> {
            JFrame a = new CreateRideGUI(driver);
            a.setVisible(true);
        });
        operationsPanel.add(btnCreateRide);
        
        btnQueryRides = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QueryRides"));
        styleButton(btnQueryRides);
        btnQueryRides.addActionListener(e -> {
            JFrame a = new FindRidesGUI();
            a.setVisible(true);
        });
        
        btnAñadirSaldo = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.AñadirSaldo")); 
        styleButton(btnAñadirSaldo);
        btnAñadirSaldo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    BLFacade facade = MainGUI.getBusinessLogic();
                    User usuarioActual = facade.loguser(userTextField.getText(), 
                            new String(passwordField.getPassword()), btnDriver.isSelected());
                    JFrame i = new MonederoGUI(facade, usuarioActual);                
                    i.setVisible(true);
                } catch (NonexitstenUserException e1) {
                    showErrorMessage(e1.getMessage());
                }
            }
        });
        operationsPanel.add(btnAñadirSaldo);
        operationsPanel.add(btnQueryRides);
        
        btnSolicitarReserva = new JButton("Solicitar Reserva");
        styleButton(btnSolicitarReserva);
        btnSolicitarReserva.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.loguser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame i = new SolicitarReservaGUI(usuarioActual);                
                i.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnSolicitarReserva);
        
        btnVerReservas = new JButton("Ver Reservas");
        styleButton(btnVerReservas);
        btnVerReservas.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.loguser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame c = new MisReservasGUI(usuarioActual);
                c.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnVerReservas);
        
        btnSolicitudesReserva = new JButton("Solicitudes de Reserva");
        styleButton(btnSolicitudesReserva);
        btnSolicitudesReserva.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.loguser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame i = new ConfirmacionReservaGUI(usuarioActual);                
                i.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnSolicitudesReserva);
        
        btnValorarConductor = new JButton("Valorar Conductor");
        styleButton(btnValorarConductor);
        btnValorarConductor.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.loguser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame j = new AddReviewGUI(usuarioActual);
                j.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnValorarConductor);
        
        btnMisValoraciones = new JButton("Mis Valoraciones");
        styleButton(btnMisValoraciones);
        btnMisValoraciones.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.loguser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame j = new VerMisValoraciones(usuarioActual);
                j.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnMisValoraciones);
        
        // Disable all buttons until login
        setButtonsEnabled(false);
        
        // ========== LOGIN PANEL ==========
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(backgroundColor);
        loginPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            "Acceso",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            headerFont,
            textColor
        ));
        
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(backgroundColor);

        ResourceBundle resourceBundle = ResourceBundle.getBundle("Etiquetas");

        lblEmail = new JLabel(resourceBundle.getString("Login.Email"));
        lblEmail.setFont(basicFont);
        lblEmail.setPreferredSize(new Dimension(100, 25));
        userTextField = new JTextField(15);
        userTextField.setFont(basicFont);

        emailPanel.add(lblEmail);
        emailPanel.add(userTextField);
        loginPanel.add(emailPanel);

        // Password field
        JPanel pswdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pswdPanel.setBackground(backgroundColor);

        lblPswd = new JLabel(resourceBundle.getString("Login.Password"));
        lblPswd.setFont(basicFont);
        lblPswd.setPreferredSize(new Dimension(100, 25));
        passwordField = new JPasswordField(15);
        passwordField.setFont(basicFont);

        pswdPanel.add(lblPswd);
        pswdPanel.add(passwordField);
        loginPanel.add(pswdPanel);

        // Name field (for registration)
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(backgroundColor);

        lblName = new JLabel(resourceBundle.getString("Login.Name"));
        lblName.setFont(basicFont);
        lblName.setPreferredSize(new Dimension(100, 25));
        nameTextField = new JTextField(15);
        nameTextField.setFont(basicFont);

        namePanel.add(lblName);
        namePanel.add(nameTextField);
        loginPanel.add(namePanel);

        // User type selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(backgroundColor);

        lblType = new JLabel(resourceBundle.getString("Login.Type"));
        lblType.setFont(basicFont);
        lblType.setPreferredSize(new Dimension(100, 25));

        btnUser = new JRadioButton(resourceBundle.getString("Login.UserType"));
        btnUser.setBackground(backgroundColor);
        btnUser.setFont(basicFont);

        btnDriver = new JRadioButton(resourceBundle.getString("Login.DriverType"));
        btnDriver.setBackground(backgroundColor);
        btnDriver.setFont(basicFont);

        userTypeGroup.add(btnUser);
        userTypeGroup.add(btnDriver);

        typePanel.add(lblType);
        typePanel.add(btnUser);
        typePanel.add(btnDriver);
        loginPanel.add(typePanel);

        // Login/Register buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(backgroundColor);

        btnLogin = new JButton(resourceBundle.getString("Login.LoginButton"));
        styleButton(btnLogin);
        btnLogin.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                if (userTextField.getText().isEmpty() || passwordField.getPassword().length == 0 || 
                    (!btnUser.isSelected() && !btnDriver.isSelected())) {
                    showErrorMessage(resourceBundle.getString("Login.MissingLoginData"));
                } else {
                    User usuarioActual = facade.loguser(userTextField.getText(), 
                                new String(passwordField.getPassword()), btnDriver.isSelected());
                    if (usuarioActual.getdriver()) {
                        driver.setEmail(usuarioActual.getEmail());
                        driver.setName(usuarioActual.getnombre());
                    }
                    
                    if (btnDriver.isSelected()) {
                        showSuccessMessage(resourceBundle.getString("Login.LoginSuccessDriver"));
                        // Enable driver-specific buttons
                        btnCreateRide.setEnabled(true);
                        btnQueryRides.setEnabled(true);
                        btnSolicitarReserva.setEnabled(false);
                        btnVerReservas.setEnabled(false);
                        btnSolicitudesReserva.setEnabled(true);
                        btnValorarConductor.setEnabled(false);
                        btnMisValoraciones.setEnabled(true);
                        btnAñadirSaldo.setEnabled(true);
                    } else {
                        showSuccessMessage(resourceBundle.getString("Login.LoginSuccessUser"));
                        // Enable user-specific buttons
                        btnCreateRide.setEnabled(false);
                        btnQueryRides.setEnabled(false);
                        btnSolicitarReserva.setEnabled(true);
                        btnVerReservas.setEnabled(true);
                        btnSolicitudesReserva.setEnabled(false);
                        btnValorarConductor.setEnabled(true);
                        btnMisValoraciones.setEnabled(false);
                        btnAñadirSaldo.setEnabled(true);
                    }
                }
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });

        btnRegister = new JButton(resourceBundle.getString("Login.RegisterButton"));
        styleButton(btnRegister);
        btnRegister.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                if (userTextField.getText().isEmpty() || passwordField.getPassword().length == 0 || 
                    (!btnUser.isSelected() && !btnDriver.isSelected()) || nameTextField.getText().isEmpty()) {
                    showErrorMessage(resourceBundle.getString("Login.MissingRegisterData"));
                } else {
                    facade.createUser(userTextField.getText(), new String(passwordField.getPassword()), 
                                      btnUser.isSelected() || btnDriver.isSelected(), nameTextField.getText());
                    showSuccessMessage(resourceBundle.getString("Login.RegisterSuccess"));
                }
            } catch (UserAlredyExistException e1) {
                showErrorMessage(e1.getMessage());
            }
        });

        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);
        loginPanel.add(btnPanel);

        // Add message label
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setBackground(backgroundColor);
        messagePanel.add(statusLabel);
        loginPanel.add(messagePanel);

        // Add panels to center
        centerPanel.add(operationsPanel, BorderLayout.WEST);
        centerPanel.add(loginPanel, BorderLayout.EAST);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
    }
    
    /**
     * Apply consistent style to button
     */
    private void styleButton(JButton button) {
        button.setFont(basicFont);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        // Add rollover effect
        button.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel) e.getSource();
            if (model.isRollover()) {
                button.setBackground(buttonHoverColor);
            } else {
                button.setBackground(primaryColor);
            }
        });
    }
    
    /**
     * Set enabled state for all buttons
     */
    private void setButtonsEnabled(boolean enabled) {
        btnCreateRide.setEnabled(enabled);
        btnQueryRides.setEnabled(enabled);
        btnSolicitarReserva.setEnabled(enabled);
        btnVerReservas.setEnabled(enabled);
        btnSolicitudesReserva.setEnabled(enabled);
        btnValorarConductor.setEnabled(enabled);
        btnMisValoraciones.setEnabled(enabled);
        btnAñadirSaldo.setEnabled(enabled);
    }
    
    /**
     * Show error message
     */
    private void showErrorMessage(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(errorColor);
    }
    
    /**
     * Show success message
     */
    private void showSuccessMessage(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(successColor);
    }
    
    /**
     * Update UI language
     */
    private void paintAgain() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Etiquetas");
        this.setTitle(resourceBundle.getString("MainGUI.MainTitle") + " - " + driver.getName());
        btnCreateRide.setText(resourceBundle.getString("MainGUI.CreateRide"));
        btnQueryRides.setText(resourceBundle.getString("MainGUI.QueryRides"));
        btnAñadirSaldo.setText(resourceBundle.getString("MainGUI.AñadirSaldo"));
        btnSolicitarReserva.setText(resourceBundle.getString("MainGUI.SolicitarReserva"));
        btnVerReservas.setText(resourceBundle.getString("MainGUI.VerReservas"));
        btnSolicitudesReserva.setText(resourceBundle.getString("MainGUI.SolicitudesReserva"));
        btnValorarConductor.setText(resourceBundle.getString("MainGUI.ValorarConductor"));
        btnMisValoraciones.setText(resourceBundle.getString("MainGUI.MisValoraciones"));
        lblEmail.setText(resourceBundle.getString("Login.Email"));
        lblPswd.setText(resourceBundle.getString("Login.Password"));
        lblName.setText(resourceBundle.getString("Login.Name"));
        lblType.setText(resourceBundle.getString("Login.Type"));
        btnUser.setText(resourceBundle.getString("Login.UserType"));
        btnDriver.setText(resourceBundle.getString("Login.DriverType"));
        btnLogin.setText(resourceBundle.getString("Login.LoginButton"));
        btnRegister.setText(resourceBundle.getString("Login.RegisterButton"));
    }
}