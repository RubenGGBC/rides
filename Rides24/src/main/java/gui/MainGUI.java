package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import domain.Driver;
import domain.User;
import domain.CuentaBancaria;
import domain.Monedero;
import exceptions.UserAlredyExistException;
import exceptions.NonexitstenUserException;
import exceptions.MonederoNoExisteException;
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
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainGUI extends JFrame {
    
    private Driver driver;
    private static final long serialVersionUID = 1L;
    private static BLFacade appFacadeInterface;
    private User currentUser = null;
    
    private static final String SMTP_HOST = "smtp.ehu.es";
    private static final String REMITENTE = "rides@rides.com";
    
    
    private Color primaryColor = new Color(70, 130, 180); // Steel Blue
    private Color buttonHoverColor = new Color(100, 149, 237); // Cornflower Blue
    private Color backgroundColor = Color.WHITE;
    private Color textColor = new Color(51, 51, 51); // Dark gray
    private Color successColor = new Color(46, 139, 87); // Sea Green
    private Color errorColor = new Color(220, 20, 60); // Crimson
    
    private Font basicFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 14);
     
    private JPanel contentPane;
    protected JLabel statusLabel;
    
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
    
    private JTextField txtCuentaBancaria;
    private JButton btnAsociarCuenta;
    private JLabel lblCuentaAsociada;
    
    private JLabel lblEmail;
    private JLabel lblPswd;
    private JLabel lblName;
    private JLabel lblType;
    private JLabel lblCuentaBancaria;
    private JButton btnLogin;
    private JButton btnRegister;
    
    private JButton btnCreateRide;
    private JButton btnQueryRides;
    private JButton btnSolicitarReserva;
    private JButton btnVerReservas;
    private JButton btnSolicitudesReserva;
    private JButton btnValorarConductor;
    private JButton btnMisValoraciones;
    private JButton btnAñadirSaldo;
    
    private ResourceBundle resourceBundle;
    
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
        
        resourceBundle = ResourceBundle.getBundle("Etiquetas");
        
        this.setSize(850, 550);
        this.setTitle(resourceBundle.getString("MainGUI.MainTitle") + " - " + driver.getName());
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
        
        JLabel titleLabel = new JLabel(resourceBundle.getString("MainGUI.MainTitle"));
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
        
        btnCreateRide = new JButton(resourceBundle.getString("MainGUI.CreateRide"));
        styleButton(btnCreateRide);
        btnCreateRide.addActionListener(e -> {
            JFrame a = new CreateRideGUI(driver);
            a.setVisible(true);
        });
        operationsPanel.add(btnCreateRide);
        
        btnQueryRides = new JButton(resourceBundle.getString("MainGUI.QueryRides"));
        styleButton(btnQueryRides);
        btnQueryRides.addActionListener(e -> {
            JFrame a = new FindRidesGUI();
            a.setVisible(true);
        });
        
        btnAñadirSaldo = new JButton(resourceBundle.getString("MainGUI.AñadirSaldo")); 
        styleButton(btnAñadirSaldo);
        btnAñadirSaldo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    BLFacade facade = MainGUI.getBusinessLogic();
                    User usuarioActual = facade.logUser(userTextField.getText(), 
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
        
        btnSolicitarReserva = new JButton(resourceBundle.getString("MainGUI.SolicitarReserva"));
        styleButton(btnSolicitarReserva);
        btnSolicitarReserva.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.logUser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame i = new SolicitarReservaGUI(usuarioActual);                
                i.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnSolicitarReserva);
        
        btnVerReservas = new JButton(resourceBundle.getString("MainGUI.VerReservas"));
        styleButton(btnVerReservas);
        btnVerReservas.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.logUser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame c = new MisReservasGUI(usuarioActual);
                c.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnVerReservas);
        
        btnSolicitudesReserva = new JButton(resourceBundle.getString("MainGUI.SolicitudesReserva"));
        styleButton(btnSolicitudesReserva);
        btnSolicitudesReserva.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.logUser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame i = new ConfirmacionReservaGUI(usuarioActual);                
                i.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnSolicitudesReserva);
        
        btnValorarConductor = new JButton(resourceBundle.getString("MainGUI.ValorarConductor"));
        styleButton(btnValorarConductor);
        btnValorarConductor.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.logUser(userTextField.getText(), 
                        new String(passwordField.getPassword()), btnDriver.isSelected());
                JFrame j = new AddReviewGUI(usuarioActual);
                j.setVisible(true);
            } catch (NonexitstenUserException e1) {
                showErrorMessage(e1.getMessage());
            }
        });
        operationsPanel.add(btnValorarConductor);
        
        btnMisValoraciones = new JButton(resourceBundle.getString("MainGUI.MisValoraciones"));
        styleButton(btnMisValoraciones);
        btnMisValoraciones.addActionListener(e -> {
            try {
                BLFacade facade = MainGUI.getBusinessLogic();
                User usuarioActual = facade.logUser(userTextField.getText(), 
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
        
        // Bank account panel
        JPanel bankPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bankPanel.setBackground(backgroundColor);
        
        lblCuentaBancaria = new JLabel(resourceBundle.getString("CuentaBancaria"));
        lblCuentaBancaria.setFont(basicFont);
        lblCuentaBancaria.setPreferredSize(new Dimension(100, 25));
        
        txtCuentaBancaria = new JTextField(15);
        txtCuentaBancaria.setFont(basicFont);
        txtCuentaBancaria.setToolTipText("Formato: ES + 22 dígitos");
        
        bankPanel.add(lblCuentaBancaria);
        bankPanel.add(txtCuentaBancaria);
        loginPanel.add(bankPanel);
        
        btnAsociarCuenta = new JButton(resourceBundle.getString("Asociar"));
        bankPanel.add(btnAsociarCuenta);
        styleButton(btnAsociarCuenta);
        btnAsociarCuenta.setEnabled(false);
        btnAsociarCuenta.addActionListener(e -> asociarCuentaBancaria());
        
        btnAsociarCuenta.setEnabled(true);
        
        JPanel associatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        associatePanel.setBackground(backgroundColor);
        
        lblCuentaAsociada = new JLabel("");
        lblCuentaAsociada.setFont(basicFont);
        associatePanel.add(lblCuentaAsociada);
        loginPanel.add(associatePanel);

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
                    User usuarioActual = facade.logUser(userTextField.getText(), 
                                new String(passwordField.getPassword()), btnDriver.isSelected());
                    currentUser = usuarioActual;
                    
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
    
    
    private void showErrorMessage(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(errorColor);
    }
    
   
    private void showSuccessMessage(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(successColor);
    }
    

    private void asociarCuentaBancaria() {
        try {
            String numeroCuenta = txtCuentaBancaria.getText().trim();
            if (numeroCuenta.isEmpty()) {
                showErrorMessage(resourceBundle.getString("ErrorNumeroCuentaVacio"));
                return;
            }

            if (!numeroCuenta.matches("^ES[0-9]{22}$")) {
                showErrorMessage(resourceBundle.getString("ErrorFormatoIBAN"));
                return;
            }

            CuentaBancaria cuentaBancaria = new CuentaBancaria(numeroCuenta);

            System.out.println("Asociando cuenta: " + numeroCuenta + " al usuario: " + currentUser.getEmail());

            BLFacade businessLogic = MainGUI.getBusinessLogic();
            Monedero monedero = businessLogic.asociarCuentaBancaria(currentUser.getEmail(), cuentaBancaria);

            if (monedero != null && monedero.getCuentaBancaria() != null) {
                System.out.println("Cuenta asociada exitosamente: " + monedero.getCuentaBancaria().getNumerotarjeta());


                enviarCorreoAsociacionCuenta(numeroCuenta);

                showSuccessMessage(resourceBundle.getString("CuentaAsociadaExitoso"));
            } else {
                System.out.println("Error: La cuenta no se asoció correctamente");
                showErrorMessage(resourceBundle.getString("ErrorAsociacionCuenta"));
            }
        } catch (MonederoNoExisteException e) {
            System.err.println("Error: Monedero no existe - " + e.getMessage());
            showErrorMessage(resourceBundle.getString("ErrorMonederoNoExiste"));
        } catch (NonexitstenUserException e) {
            System.err.println("Error: Usuario no existe - " + e.getMessage());
            showErrorMessage(resourceBundle.getString("ErrorUsuarioNoExiste"));
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            showErrorMessage(resourceBundle.getString("ErrorGenerico") + ": " + e.getMessage());
        }
    }

    /**
     * Send confirmation email for bank account association
     */
    private void enviarCorreoAsociacionCuenta(String numeroCuenta) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);

            Session session = Session.getInstance(props);

            String receptor = currentUser.getEmail();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor));
            message.setSubject(resourceBundle.getString("EmailAsociacionAsunto"));

            String cuentaSegura = numeroCuenta.substring(0, 4) + "..." + numeroCuenta.substring(numeroCuenta.length() - 4);
            String cuerpoMensaje = String.format(
                resourceBundle.getString("EmailAsociacionMensaje"),
                currentUser.getnombre(),
                cuentaSegura
            );

            message.setText(cuerpoMensaje);

            Transport.send(message);

            System.out.println("Correo de confirmación de asociación de cuenta enviado a: " + receptor);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de confirmación de asociación de cuenta: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar correo de asociación de cuenta: " + e.getMessage());
            e.printStackTrace(); 
        }
    }
    
    
   
     
    
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
        lblCuentaBancaria.setText(resourceBundle.getString("CuentaBancaria"));
        btnAsociarCuenta.setText(resourceBundle.getString("Asociar"));

    }
}