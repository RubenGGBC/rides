package gui;

import java.awt.Color;
import java.net.URL;
import java.util.Locale;

import javax.swing.UIManager;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Driver;
import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import exceptions.UserAlredyExistException;

public class ApplicationLauncher { 
	
	
	
	public static void main(String[] args) throws UserAlredyExistException {

		ConfigXML c=ConfigXML.getInstance();
	
		System.out.println(c.getLocale());
		
		Locale.setDefault(new Locale(c.getLocale()));
		
		System.out.println("Locale: "+Locale.getDefault());
		
	    Driver driver=new Driver("driver3@gmail.com","Test Driver");

		
		MainGUI a=new MainGUI(driver);
		a.setVisible(true);


        BLFacade appFacadeInterface = new BLFactory().getBussinessLogic(c);

        MainGUI.setBussinessLogic(appFacadeInterface);



	}

}
