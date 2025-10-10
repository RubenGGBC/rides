package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.CardLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
// he añadido un comentario para la integracion continua
public class AñadirSaldo extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AñadirSaldo frame = new AñadirSaldo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 
	}

	/**
	 * Create the frame.
	 */
	public AñadirSaldo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton AñadirMetodoPago = new JButton("Ingresar al monedero");
		AñadirMetodoPago.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		AñadirMetodoPago.setBounds(201, 229, 181, 20);
		contentPane.add(AñadirMetodoPago);
		
		textField = new JTextField();
		textField.setBounds(197, 119, 121, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(197, 88, 121, 20);
		contentPane.add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(197, 49, 121, 20);
		contentPane.add(textField_2);
		
		JLabel lblNewLabel = new JLabel("Numero de tarjeta:");
		lblNewLabel.setBounds(34, 52, 105, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblCvv = new JLabel("CVV:");
		lblCvv.setBounds(34, 91, 76, 14);
		contentPane.add(lblCvv);
		
		JLabel lblFechaDeCaducidad = new JLabel("Fecha de caducidad:");
		lblFechaDeCaducidad.setBounds(34, 122, 105, 14);
		contentPane.add(lblFechaDeCaducidad);
		
		JLabel lblCantidad = new JLabel("Cantidad");
		lblCantidad.setBounds(44, 147, 160, 14);
		contentPane.add(lblCantidad);
		 
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(197, 144, 121, 20);
		contentPane.add(textField_3);
		
		JButton btnIngresarALa = new JButton("Ingresar a la cuenta");
		btnIngresarALa.setBounds(10, 229, 181, 20);
		contentPane.add(btnIngresarALa);
	}
}
