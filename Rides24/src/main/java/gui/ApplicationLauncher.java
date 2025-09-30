package gui;

import java.net.URL;
import java.util.Locale;

import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Driver;
import businesslogic.BLFacade;
import businesslogic.BLFacadeImplementation;

public class ApplicationLauncher { 
	
	static Logger logger = Logger.getLogger(ApplicationLauncher.class.getName());
	
	public static void main(String[] args) {

		ConfigXML c=ConfigXML.getInstance();
	
		logger.info(c.getLocale());
		
		Locale.setDefault(new Locale(c.getLocale()));
		
		System.out.println("Locale: "+Locale.getDefault());
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting Look and Feel: " + e.getMessage());
		}
		
	    Driver driver=new Driver("driver3@gmail.com","Test Driver");

		
		MainGUI a=new MainGUI(driver);
		a.setVisible(true);


		try {
			
			BLFacade appFacadeInterface;
			
			if (c.isBusinessLogicLocal()) {
			
				DataAccess da= new DataAccess();
				appFacadeInterface=new BLFacadeImplementation(da);

				
			} 
			
			else { //If remote
				
				 String serviceName= "http://"+c.getBusinessLogicNode() +":"+ c.getBusinessLogicPort()+"/ws/"+c.getBusinessLogicName()+"?wsdl";
				 
				URL url = new URL(serviceName);

		 
		        //1st argument refers to wsdl document above
				//2nd argument is service name, refer to wsdl document above
		        QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
		 
		        Service service = Service.create(url, qname);

		         appFacadeInterface = service.getPort(BLFacade.class);
			} 
			
			MainGUI.setBussinessLogic(appFacadeInterface);

		

			
		}catch (Exception e) {
			
			
			System.out.println("Error in ApplicationLauncher: "+e.toString());
		}
		//a.pack();


	}

}
