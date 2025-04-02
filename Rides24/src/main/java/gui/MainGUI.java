package gui;

/**
 * @author Software Engineering teachers
 */


import javax.swing.*;

import domain.Driver;
import domain.Ride;
import domain.User;
import gui.MainGUI;
import exceptions.UserAlredyExistException;
import exceptions.NonexitstenUserException;
import businessLogic.BLFacade;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class MainGUI extends JFrame {
	
    private Driver driver;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JButton jButtonCreateQuery = null;
	private JButton jButtonQueryQueries = null;

    private static BLFacade appFacadeInterface;
	
	public static BLFacade getBusinessLogic(){
		return appFacadeInterface;
	}
	 
	public static void setBussinessLogic (BLFacade afi){
		appFacadeInterface=afi;
	}
	protected JLabel jLabelSelectOption;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnNewRadioButton_2;
	private JPanel panel;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField usertxt;
	private JTextField textname;
	private JPasswordField psswdtxt;
	
	/**
	 * This is the default constructor
	 */
	public MainGUI(Driver d) {
		super();

		driver=d;
		
		// this.setSize(271, 295);
		this.setSize(926, 544);
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setBounds(10, 0, 239, 62);
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		rdbtnNewRadioButton = new JRadioButton("English");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				System.out.println("Locale: "+Locale.getDefault());
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton);
		
		rdbtnNewRadioButton_1 = new JRadioButton("Euskara");
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				System.out.println("Locale: "+Locale.getDefault());
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		rdbtnNewRadioButton_2 = new JRadioButton("Castellano");
		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				System.out.println("Locale: "+Locale.getDefault());
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_2);
	
		panel = new JPanel();
		panel.setBounds(10, 246, 239, 62);
		panel.add(rdbtnNewRadioButton_1);
		panel.add(rdbtnNewRadioButton_2);
		panel.add(rdbtnNewRadioButton);
		
		jButtonCreateQuery = new JButton();
		jButtonCreateQuery.setBounds(10, 71, 239, 62);
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateRide"));
		jButtonCreateQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CreateRideGUI(driver);
				a.setVisible(true);
			}
		});
		
		jButtonQueryQueries = new JButton();
		jButtonQueryQueries.setBounds(10, 143, 239, 62);
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QueryRides"));
		jButtonQueryQueries.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new FindRidesGUI();

				a.setVisible(true);
			}
		});
	
		jContentPane = new JPanel();
		jContentPane.setLayout(null);
		
		usertxt = new JTextField();
		usertxt.setBounds(512, 87, 204, 30);
		usertxt.setText("");		jContentPane.add(usertxt);
		usertxt.setColumns(10);
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonCreateQuery);
		jContentPane.add(jButtonQueryQueries);
		jContentPane.add(panel);
		
		
		setContentPane(jContentPane);
		
		JLabel Userlbl = new JLabel("Introduce tu email"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-2$
		Userlbl.setBounds(337, 95, 138, 14);
		jContentPane.add(Userlbl);
		 
		JLabel psdlbl = new JLabel("Introduce tu contraseña"); //$NON-NLS-1$ //$NON-NLS-2$
		psdlbl.setBounds(329, 142, 146, 14);
		jContentPane.add(psdlbl);
		
		
		JLabel lblName = new JLabel("Introduce tu nombre, solo si vas a registrarte"); //$NON-NLS-1$ //$NON-NLS-2$
		lblName.setBounds(277, 191, 240, 14);
		jContentPane.add(lblName);
		
		textname = new JTextField();
		textname.setBounds(512, 183, 204, 31);
		jContentPane.add(textname);
		textname.setColumns(10);
		
		JRadioButton btnDriver = new JRadioButton("Driver"); //$NON-NLS-1$ //$NON-NLS-2$
		
		btnDriver.setBounds(580, 239, 109, 23);
		jContentPane.add(btnDriver);
		
		JRadioButton btnUser = new JRadioButton("Usuario"); //$NON-NLS-1$ //$NON-NLS-2$
		btnUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDriver.setSelected(false);
				
				
			
		}});
		btnUser.setBounds(408, 239, 109, 23);
		jContentPane.add(btnUser);
		
		btnDriver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnUser.setSelected(false);
				
			}
		});
		JButton jbuttonSolicitarR = new JButton();
		jbuttonSolicitarR.setText("SolicitarReserva");
		jbuttonSolicitarR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				User usuarioActual = null;
				try {
					usuarioActual = facade.loguser(usertxt.getText(), psswdtxt.getText(), btnDriver.isSelected());
				} catch (NonexitstenUserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JFrame i = new SolicitarReservaGUI(usuarioActual);				
				i.setVisible(true);	
				
			}
		});
		jbuttonSolicitarR.setBounds(20, 322, 239, 62);
		jContentPane.add(jbuttonSolicitarR);
		
		JButton btnVerR = new JButton("VER RESERVAS");//$NON-NLS-1$ //$NON-NLS-2$
		btnVerR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				User usuarioActual = null;
				BLFacade facade = MainGUI.getBusinessLogic();
				try {
					usuarioActual=facade.loguser(usertxt.getText(), psswdtxt.getText(), btnDriver.isSelected());
				} catch (NonexitstenUserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JFrame c =new MisReservasGUI(usuarioActual);
				c.setVisible(true);
				
			}
		});
		btnVerR.setBounds(20, 395, 239, 56);
		jContentPane.add(btnVerR);
		
	
		JLabel jLabelMsg_1 = new JLabel(""); //$NON-NLS-1$ //$NON-NLS-2$
		jLabelMsg_1.setBounds(450, 20, 266, 56);
		jContentPane.add(jLabelMsg_1);
		
		
		JButton btnRegister = new JButton("Register"); //$NON-NLS-1$ //$NON-NLS-2$
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BLFacade facade = MainGUI.getBusinessLogic();
					if(usertxt.getText().isEmpty() || psswdtxt.getText().equals(null) || (btnUser.isSelected()==false &&  btnDriver.isSelected()==false) || textname.getText().isEmpty() ) {
						jLabelMsg_1.setText("Faltan datos");
					}else {
						facade.createUser(usertxt.getText(), psswdtxt.getText(),btnUser.isSelected() || btnDriver.isSelected(),textname.getText());
						jLabelMsg_1.setText("Se ha crado la cuenta");
					}
				} catch (UserAlredyExistException e1) {
					// TODO Auto-generated catch block
					jLabelMsg_1.setText(e1.getMessage());
				}
			}
		});
			
		btnRegister.setBounds(572, 298, 99, 23);
		jContentPane.add(btnRegister);
		
		psswdtxt = new JPasswordField(); //$NON-NLS-1$ //$NON-NLS-2$
		psswdtxt.setBounds(512, 128, 204, 30);
		jContentPane.add(psswdtxt);
		
		JButton btnSR = new JButton("Solicitudes de reserva"); //$NON-NLS-1$ //$NON-NLS-2$
		btnSR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				User usuarioActual = null;
				try {
					usuarioActual = facade.loguser(usertxt.getText(), psswdtxt.getText(), btnDriver.isSelected());
				} catch (NonexitstenUserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JFrame i = new ConfirmacionReservaGUI(usuarioActual);				
				i.setVisible(true);	
				
			}
		});
		btnSR.setBounds(354, 352, 163, 43);
		jContentPane.add(btnSR);
		
		JButton btnValorarC = new JButton("Valorar conductor por viajes"); //$NON-NLS-1$ //$NON-NLS-2$
		btnValorarC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				User usuarioActual = null;
				try {
					usuarioActual = facade.loguser(usertxt.getText(), psswdtxt.getText(), btnDriver.isSelected());
				} catch (NonexitstenUserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JFrame j = new AddReviewGUI(usuarioActual);
				j.setVisible(true);
				
			}
		});
		btnValorarC.setBounds(580, 357, 223, 33);
		jContentPane.add(btnValorarC);
		
		JButton btnMisVal = new JButton("Mis Valoraciones"); //$NON-NLS-1$ //$NON-NLS-2$
		btnMisVal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				User usuarioActual = null;
				try {
					usuarioActual = facade.loguser(usertxt.getText(), psswdtxt.getText(), btnDriver.isSelected());
				} catch (NonexitstenUserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JFrame j = new VerMisValoraciones(usuarioActual);
				j.setVisible(true);
			}
		});
		btnMisVal.setBounds(491, 412, 132, 30);
		jContentPane.add(btnMisVal);
		
		
		
	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	
	
	JButton Logrbtn = new JButton("Iniciar"); //$NON-NLS-1$ //$NON-NLS-2$
	Logrbtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			try {
				BLFacade facade = MainGUI.getBusinessLogic();
				if(usertxt.getText().isEmpty() || psswdtxt.getText().isEmpty() || (btnUser.isSelected()==false &&  btnDriver.isSelected()==false)) {
					System.out.println("Faltan datos");
				}else {
					User usuarioActual;
					usuarioActual=facade.loguser(usertxt.getText(), psswdtxt.getText(), btnDriver.isSelected());
					if(usuarioActual.getdriver()==true) {
						driver.setEmail(usuarioActual.getEmail());
						driver.setName(usuarioActual.getnombre());
					}
					
					if(btnDriver.isSelected()) {
						jButtonCreateQuery.setEnabled(true);
						jButtonQueryQueries.setEnabled(true);	
						jbuttonSolicitarR.setEnabled(false);
						btnVerR.setEnabled(false);
						btnSR.setEnabled(true);
						btnValorarC.setEnabled(false);
						btnMisVal.setEnabled(true);
					
						
						jLabelMsg_1.setText("Sesion iniciada como Driver");

						
						
					}else {
						jButtonCreateQuery.setEnabled(false);
						jButtonQueryQueries.setEnabled(false);	
						jbuttonSolicitarR.setEnabled(true);
						btnVerR.setEnabled(true);
						btnSR.setEnabled(false);
						btnValorarC.setEnabled(true);
						btnMisVal.setEnabled(false);
					
						jLabelMsg_1.setText("Sesion iniciada como User");

					}

				}
			} catch (NonexitstenUserException e1) {
				jLabelMsg_1.setText(e1.getMessage());
			}
		}});
	Logrbtn.setBounds(399, 298, 89, 23);
	jContentPane.add(Logrbtn);
	}
	
	private void paintAgain() {
		jLabelSelectOption.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QueryRides"));
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateRide"));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle")+ " - driver :"+driver.getName());
	}
} // @jve:decl-index=0:visual-constraint="0,0"

