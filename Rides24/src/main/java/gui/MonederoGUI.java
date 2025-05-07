package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.ResourceBundle;

// Importaciones para envío de correo
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

/**
 * Interfaz gráfica para gestionar el monedero electrónico
 */
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
    private static final String REMITENTE = "cesar@cesar.com";
    
    /**
     * Constructor de la interfaz
     * 
     * @param bl lógica de negocio
     * @param user usuario actual
     */
    public MonederoGUI(BLFacade bl, User user) {
        businessLogic = bl;
        currentUser = user;
        resourceBundle = ResourceBundle.getBundle("Etiquetas");
        
        try {
            jbInit();
            updateSaldoDisplay();
            updateCuentaBancariaDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void jbInit() throws Exception {
        setTitle(resourceBundle.getString("Monedero"));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        
        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBackground(BACKGROUND_COLOR);
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        // Título
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
        
        // Panel de cuenta bancaria
        JPanel panelCuenta = new JPanel();
        panelCuenta.setLayout(null);
        panelCuenta.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("CuentaBancaria")));
        panelCuenta.setBackground(BACKGROUND_COLOR);
        panelCuenta.setBounds(10, 249, 414, 70);
        contentPane.add(panelCuenta);
        
        // Etiqueta de cuenta bancaria
        lblCuentaBancaria = new JLabel(resourceBundle.getString("NumeroCuenta") + ":");
        lblCuentaBancaria.setFont(LABEL_FONT);
        lblCuentaBancaria.setBounds(10, 25, 120, 25);
        panelCuenta.add(lblCuentaBancaria);
        
        // Campo de texto para la cuenta bancaria
        txtCuentaBancaria = new JTextField();
        txtCuentaBancaria.setBounds(140, 25, 150, 25);
        panelCuenta.add(txtCuentaBancaria);
        
        // Botón para asociar cuenta
        btnAsociarCuenta = new JButton(resourceBundle.getString("Asociar"));
        btnAsociarCuenta.setFont(LABEL_FONT);
        btnAsociarCuenta.setBackground(BUTTON_COLOR);
        btnAsociarCuenta.setForeground(Color.WHITE);
        btnAsociarCuenta.setBounds(300, 25, 100, 25);
        btnAsociarCuenta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                asociarCuentaBancaria();
            }
        });
        panelCuenta.add(btnAsociarCuenta);
        
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
        
        // Configuración adicional
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
    
    /**
     * Actualiza la visualización del saldo actual
     */
    private void updateSaldoDisplay() {
        try {
            float saldo = businessLogic.consultarSaldo(currentUser.getEmail());
            lblSaldo.setText(DECIMAL_FORMAT.format(saldo) + " €");
        } catch (MonederoNoExisteException e) {
            // Si no existe el monedero, mostramos 0
            lblSaldo.setText("0.00 €");
            
            // Intentamos crear un monedero
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
     * Actualiza la visualización de la cuenta bancaria
     */
    private void updateCuentaBancariaDisplay() {
        try {
            System.out.println("Actualizando visualización de cuenta bancaria para usuario: " + currentUser.getEmail());
            
            Monedero monedero = businessLogic.getMonedero(currentUser.getEmail());
            if (monedero != null) {
                CuentaBancaria cuenta = monedero.getCuentaBancaria();
                if (cuenta != null && cuenta.getNumerotarjeta() != null && !cuenta.getNumerotarjeta().isEmpty()) {
                    System.out.println("Cuenta encontrada: " + cuenta.getNumerotarjeta());
                    txtCuentaBancaria.setText(cuenta.getNumerotarjeta());
                    // Opcionalmente, deshabilitar edición si ya hay una cuenta asociada
                    // txtCuentaBancaria.setEditable(false);
                    // btnAsociarCuenta.setText(resourceBundle.getString("CambiarCuenta"));
                } else {
                    System.out.println("No hay cuenta asociada");
                    txtCuentaBancaria.setText("");
                    // txtCuentaBancaria.setEditable(true);
                    // btnAsociarCuenta.setText(resourceBundle.getString("Asociar"));
                }
            } else {
                System.out.println("Monedero no encontrado");
                txtCuentaBancaria.setText("");
            }
        } catch (MonederoNoExisteException e) {
            System.err.println("Error al actualizar visualización: Monedero no existe - " + e.getMessage());
            txtCuentaBancaria.setText("");
        } catch (NonexitstenUserException e) {
            System.err.println("Error al actualizar visualización: Usuario no existe - " + e.getMessage());
            txtCuentaBancaria.setText("");
        } catch (Exception e) {
            System.err.println("Error inesperado al actualizar visualización: " + e.getMessage());
            e.printStackTrace();
            txtCuentaBancaria.setText("");
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
                showError("No tienes tanto saldo en la cuenta");
                return;
            }
            businessLogic.ingresarDinero(currentUser.getEmail(), cantidad);
            
            // Actualizar la visualización del saldo
            updateSaldoDisplay();
            txtCantidad.setText("");
            
            // Enviar correo de confirmación
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
    
    /**
     * Envía un correo de confirmación con los detalles del ingreso
     * 
     * @param cantidad La cantidad ingresada al monedero
     */
    private void enviarCorreoConfirmacion(float cantidad) {
        try {
            // Configurar propiedades de correo (similar al ejemplo EnviarCorreo)
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            
            // Crear sesión sin autenticación
            Session session = Session.getInstance(props);
            
            // Obtener el correo del usuario actual
            String receptor = currentUser.getEmail();
            
            // Crear el mensaje de correo
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor));
            message.setSubject("Confirmación de ingreso en su Monedero");
            
            // Formato del cuerpo del mensaje
            String cuerpoMensaje = "Estimado/a " + currentUser.getnombre() + ":\n\n" +
                    "Le confirmamos que se ha realizado un ingreso en su Monedero por importe de " + 
                    DECIMAL_FORMAT.format(cantidad) + " €.\n\n" +
                    "Su saldo actual es de " + lblSaldo.getText() + ".\n\n" +
                    "Gracias por usar nuestros servicios.\n\n" +
                    "Atentamente,\nEl equipo de Monedero";
            
            message.setText(cuerpoMensaje);
            
            // Enviar el mensaje
            Transport.send(message);
            
            System.out.println("Correo de confirmación enviado a: " + receptor);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de confirmación: " + e.getMessage());
            e.printStackTrace();
            // No mostramos error al usuario para no interrumpir la operación principal
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Retira dinero del monedero
     */
    private void retirarDinero() {
        try {
            String cantidadStr = txtCantidad.getText().trim();
            if (cantidadStr.isEmpty()) {
                showError(resourceBundle.getString("ErrorCantidadVacia"));
                return;
            }
            
            float cantidad = Float.parseFloat(cantidadStr);
            businessLogic.retirarDinero(currentUser.getEmail(), cantidad);
            
            // Actualizar la visualización del saldo
            updateSaldoDisplay();
            txtCantidad.setText("");
            
            // Enviar correo de confirmación de retiro
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
    
    /**
     * Envía un correo de confirmación con los detalles del retiro
     * 
     * @param cantidad La cantidad retirada del monedero
     */
    private void enviarCorreoRetiro(float cantidad) {
        try {
            // Configurar propiedades de correo
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            
            // Crear sesión sin autenticación
            Session session = Session.getInstance(props);
            
            // Obtener el correo del usuario actual
            String receptor = currentUser.getEmail();
            
            // Crear el mensaje de correo
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor));
            message.setSubject("Confirmación de retiro de su Monedero");
            
            // Formato del cuerpo del mensaje
            String cuerpoMensaje = "Estimado/a " + currentUser.getnombre() + ":\n\n" +
                    "Le confirmamos que se ha realizado un retiro de su Monedero por importe de " + 
                    DECIMAL_FORMAT.format(cantidad) + " €.\n\n" +
                    "Su saldo actual es de " + lblSaldo.getText() + ".\n\n" +
                    "Gracias por usar nuestros servicios.\n\n" +
                    "Atentamente,\nEl equipo de Monedero";
            
            message.setText(cuerpoMensaje);
            
            // Enviar el mensaje
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
    
    /**
     * Asocia una cuenta bancaria al monedero
     */
    private void asociarCuentaBancaria() {
        try {
            String numeroCuenta = txtCuentaBancaria.getText().trim();
            if (numeroCuenta.isEmpty()) {
                showError(resourceBundle.getString("ErrorNumeroCuentaVacio"));
                return;
            }
            
            // Validación básica del formato de la cuenta bancaria (IBAN español)
            if (!numeroCuenta.matches("^ES[0-9]{22}$")) {
                showError(resourceBundle.getString("ErrorFormatoIBAN"));
                return;
            }
            
            // Crear la cuenta bancaria con el número proporcionado
            CuentaBancaria cuentaBancaria = new CuentaBancaria(numeroCuenta);
            
            // Mostrar un mensaje de depuración
            System.out.println("Asociando cuenta: " + numeroCuenta + " al usuario: " + currentUser.getEmail());
            
            // Llamar al método de la lógica de negocio
            Monedero monedero = businessLogic.asociarCuentaBancaria(currentUser.getEmail(), cuentaBancaria);
            
            // Verificar que la cuenta se haya asociado correctamente
            if (monedero != null && monedero.getCuentaBancaria() != null) {
                System.out.println("Cuenta asociada exitosamente: " + monedero.getCuentaBancaria().getNumerotarjeta());
                
                // Actualizar la visualización
                updateCuentaBancariaDisplay();
                
                // Enviar correo de confirmación de asociación de cuenta
                enviarCorreoAsociacionCuenta(numeroCuenta);
                
                showInfo(resourceBundle.getString("CuentaAsociadaExitoso"));
            } else {
                System.out.println("Error: La cuenta no se asoció correctamente");
                showError(resourceBundle.getString("ErrorAsociacionCuenta"));
            }
        } catch (MonederoNoExisteException e) {
            System.err.println("Error: Monedero no existe - " + e.getMessage());
            showError(resourceBundle.getString("ErrorMonederoNoExiste"));
        } catch (NonexitstenUserException e) {
            System.err.println("Error: Usuario no existe - " + e.getMessage());
            showError(resourceBundle.getString("ErrorUsuarioNoExiste"));
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            showError(resourceBundle.getString("ErrorGenerico") + ": " + e.getMessage());
        }
    }
    
    /**
     * Envía un correo de confirmación con los detalles de la asociación de cuenta
     * 
     * @param numeroCuenta El número de cuenta bancaria asociada
     */
    private void enviarCorreoAsociacionCuenta(String numeroCuenta) {
        try {
            // Configurar propiedades de correo
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            
            // Crear sesión sin autenticación
            Session session = Session.getInstance(props);
            
            // Obtener el correo del usuario actual
            String receptor = currentUser.getEmail();
            
            // Crear el mensaje de correo
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor));
            message.setSubject("Confirmación de asociación de cuenta bancaria");
            
            // Formato del cuerpo del mensaje
            String cuerpoMensaje = "Estimado/a " + currentUser.getnombre() + ":\n\n" +
                    "Le confirmamos que se ha asociado la cuenta bancaria " + 
                    numeroCuenta.substring(0, 4) + "..." + numeroCuenta.substring(numeroCuenta.length() - 4) + 
                    " a su Monedero.\n\n" +
                    "Ahora puede realizar operaciones entre su monedero y esta cuenta bancaria.\n\n" +
                    "Gracias por usar nuestros servicios.\n\n" +
                    "Atentamente,\nEl equipo de Monedero";
            
            message.setText(cuerpoMensaje);
            
            // Enviar el mensaje
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
    
    /**
     * Muestra un mensaje de error
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, resourceBundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Muestra un mensaje informativo
     */
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, resourceBundle.getString("Informacion"), JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Acciones al cerrar la ventana
     */
    private void onClose() {
        // Cualquier limpieza o guardado necesario al cerrar
    }
    
    /**
     * Método principal para pruebas
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Este main es solo para pruebas, normalmente se llamaría desde otra clase
        // MonederoGUI frame = new MonederoGUI(businessLogic, currentUser);
        // frame.setVisible(true);
    }
}