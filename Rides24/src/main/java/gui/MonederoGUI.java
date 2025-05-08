package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import businessLogic.BLFacade;
import domain.CuentaBancaria;
import domain.Monedero;
import domain.User;
import exceptions.CantidadInvalidaException;
import exceptions.MonederoNoExisteException;
import exceptions.NonexitstenUserException;
import exceptions.SaldoInsuficienteException;

public class MonederoGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JPanel contentPane;
    private JLabel lblSaldo;
    private JTextField txtCantidad;
    private JButton btnIngresar;
    private JButton btnRetirar;
    private JButton btnVolver;
    private JLabel lblTitulo;
    private JLabel lblCuentaBancaria;
    private JTextField txtCuentaBancaria;
    private JButton btnAsociarCuenta;
    
    private BLFacade businessLogic;
    private User currentUser;
    private ResourceBundle resourceBundle;
    
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    private final Color BUTTON_COLOR = new Color(70, 130, 180); // Steel Blue
    private final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    
    // Configuración para envío de correos
    private static final String SMTP_HOST = "smtp.ehu.es";
    private static final String REMITENTE = "rides@rides.com";
    
    public MonederoGUI(BLFacade bl, User user) {
        businessLogic = bl;
        currentUser = user;
        resourceBundle = ResourceBundle.getBundle("Etiquetas");
        
        try {
            jbInit();
            updateSaldoDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() throws Exception {
        // Set window title from resource bundle
        setTitle(resourceBundle.getString("Monedero"));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        
        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBackground(BACKGROUND_COLOR);
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        // Título con el nombre del usuario
        lblTitulo = new JLabel(resourceBundle.getString("Monedero") + " - " + currentUser.getnombre());
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setBounds(10, 11, 414, 35);
        contentPane.add(lblTitulo);
        
        // Panel de saldo
        JPanel panelSaldo = new JPanel();
        panelSaldo.setLayout(null);
        panelSaldo.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("SaldoActual")));
        panelSaldo.setBackground(BACKGROUND_COLOR);
        panelSaldo.setBounds(10, 57, 414, 70);
        contentPane.add(panelSaldo);
        
        // Etiqueta de saldo
        lblSaldo = new JLabel("0.00 €");
        lblSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 24));
        lblSaldo.setBounds(10, 21, 394, 38);
        panelSaldo.add(lblSaldo);
        
        // Panel de operaciones
        JPanel panelOperaciones = new JPanel();
        panelOperaciones.setLayout(null);
        panelOperaciones.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Operaciones")));
        panelOperaciones.setBackground(BACKGROUND_COLOR);
        panelOperaciones.setBounds(10, 138, 414, 100);
        contentPane.add(panelOperaciones);
        
        // Etiqueta de cantidad
        JLabel lblCantidad = new JLabel(resourceBundle.getString("Cantidad") + ":");
        lblCantidad.setFont(LABEL_FONT);
        lblCantidad.setBounds(10, 25, 80, 25);
        panelOperaciones.add(lblCantidad);
        
        // Campo de texto para la cantidad
        txtCantidad = new JTextField();
        txtCantidad.setBounds(100, 25, 100, 25);
        panelOperaciones.add(txtCantidad);
        
        // Botón para ingresar dinero
        btnIngresar = new JButton(resourceBundle.getString("Ingresar"));
        btnIngresar.setFont(LABEL_FONT);
        btnIngresar.setBackground(BUTTON_COLOR);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setBounds(220, 25, 180, 25);
        btnIngresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ingresarDinero();
            }
        });
        panelOperaciones.add(btnIngresar);
        
        // Botón para retirar dinero
        btnRetirar = new JButton(resourceBundle.getString("Retirar"));
        btnRetirar.setFont(LABEL_FONT);
        btnRetirar.setBackground(BUTTON_COLOR);
        btnRetirar.setForeground(Color.WHITE);
        btnRetirar.setBounds(220, 60, 180, 25);
        btnRetirar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                retirarDinero();
            }
        });
        panelOperaciones.add(btnRetirar);
        
 
        // Botón para volver
        btnVolver = new JButton(resourceBundle.getString("Volver"));
        btnVolver.setFont(LABEL_FONT);
        btnVolver.setBackground(new Color(192, 192, 192));
        btnVolver.setBounds(162, 330, 110, 30);
        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(btnVolver);
        
        setResizable(false);
        setLocationRelativeTo(null);
        
        // Listener para el cierre de la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
    }
    
    private void updateSaldoDisplay() {
        try {
            float saldo = businessLogic.consultarSaldo(currentUser.getEmail());
            lblSaldo.setText(DECIMAL_FORMAT.format(saldo) + " €");
        } catch (MonederoNoExisteException e) {
            lblSaldo.setText("0.00 €");
            
            try {
                businessLogic.ingresarDinero(currentUser.getEmail(), 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (NonexitstenUserException e) {
            showError(resourceBundle.getString("ErrorUsuarioNoExiste"));
        } catch (Exception e) {
            e.printStackTrace();
            showError(resourceBundle.getString("ErrorGenerico") + ": " + e.getMessage());
        }
    }
    

    
    /**
     * Ingresa dinero en el monedero
     */
    private void ingresarDinero() {
        try {
            String cantidadStr = txtCantidad.getText().trim();
            if (cantidadStr.isEmpty()) {
                showError(resourceBundle.getString("ErrorCantidadVacia"));
                return;
            }
            
            float cantidad = Float.parseFloat(cantidadStr);
            if(currentUser.getCuenta().getNumeroRandom()<cantidad) {
                // This error message should also come from the resource bundle
                showError(resourceBundle.getString("ErrorSaldoInsuficienteCuenta"));
                return;
            }
            businessLogic.ingresarDinero(currentUser.getEmail(), cantidad);
            
            updateSaldoDisplay();
            txtCantidad.setText("");
            
            enviarCorreoConfirmacion(cantidad);
            
            showInfo(resourceBundle.getString("IngresoExitoso") + ": " + DECIMAL_FORMAT.format(cantidad) + " €");
        } catch (NumberFormatException e) {
            showError(resourceBundle.getString("ErrorFormatoCantidad"));
        } catch (CantidadInvalidaException e) {
            showError(resourceBundle.getString("ErrorCantidadInvalida"));
        } catch (MonederoNoExisteException e) {
            showError(resourceBundle.getString("ErrorMonederoNoExiste"));
        } catch (NonexitstenUserException e) {
            showError(resourceBundle.getString("ErrorUsuarioNoExiste"));
        } catch (Exception e) {
            showError(resourceBundle.getString("ErrorGenerico") + ": " + e.getMessage());
        }
    }
    
    private void enviarCorreoConfirmacion(float cantidad) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            
            Session session = Session.getInstance(props);
            
            String receptor = currentUser.getEmail();
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor));
            // Email subject should use resource bundle
            message.setSubject(resourceBundle.getString("EmailIngresoAsunto"));
            
            // Email body should use resource bundle with proper formatting for the message
            String cuerpoMensaje = String.format(
                resourceBundle.getString("EmailIngresoMensaje"),
                currentUser.getnombre(),
                DECIMAL_FORMAT.format(cantidad),
                lblSaldo.getText()
            );
            
            message.setText(cuerpoMensaje);
            
            Transport.send(message);
            
            System.out.println("Correo de confirmación enviado a: " + receptor);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de confirmación: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void retirarDinero() {
        try {
            String cantidadStr = txtCantidad.getText().trim();
            if (cantidadStr.isEmpty()) {
                showError(resourceBundle.getString("ErrorCantidadVacia"));
                return;
            }
            
            float cantidad = Float.parseFloat(cantidadStr);
            businessLogic.retirarDinero(currentUser.getEmail(), cantidad);
            
            updateSaldoDisplay();
            txtCantidad.setText("");
            
            enviarCorreoRetiro(cantidad);
            
            showInfo(resourceBundle.getString("RetiroExitoso") + ": " + DECIMAL_FORMAT.format(cantidad) + " €");
        } catch (NumberFormatException e) {
            showError(resourceBundle.getString("ErrorFormatoCantidad"));
        } catch (CantidadInvalidaException e) {
            showError(resourceBundle.getString("ErrorCantidadInvalida"));
        } catch (SaldoInsuficienteException e) {
            showError(resourceBundle.getString("ErrorSaldoInsuficiente"));
        } catch (MonederoNoExisteException e) {
            showError(resourceBundle.getString("ErrorMonederoNoExiste"));
        } catch (NonexitstenUserException e) {
            showError(resourceBundle.getString("ErrorUsuarioNoExiste"));
        } catch (Exception e) {
            showError(resourceBundle.getString("ErrorGenerico") + ": " + e.getMessage());
        }
    }
    
    private void enviarCorreoRetiro(float cantidad) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            
            Session session = Session.getInstance(props);
            
            String receptor = currentUser.getEmail();
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor));
            // Email subject should use resource bundle
            message.setSubject(resourceBundle.getString("EmailRetiroAsunto"));
            
            // Email body should use resource bundle
            String cuerpoMensaje = String.format(
                resourceBundle.getString("EmailRetiroMensaje"),
                currentUser.getnombre(),
                DECIMAL_FORMAT.format(cantidad),
                lblSaldo.getText()
            );
            
            message.setText(cuerpoMensaje);
            
            Transport.send(message);
            
            System.out.println("Correo de confirmación de retiro enviado a: " + receptor);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de confirmación de retiro: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar correo de retiro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
   
 
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, resourceBundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, resourceBundle.getString("Informacion"), JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void onClose() {
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}