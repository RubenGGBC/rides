package gui;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import configuration.ConfigXML;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class BLFactory {

    public BLFacade getBusinessLogicFactory(boolean isLocal) {
        if (isLocal) {
            return new BLFacadeImplementation();
        } else {
            return createRemoteFacade();
        }
    }

    public BLFacade getBussinessLogic(ConfigXML c) {
        return getBusinessLogicFactory(c.isBusinessLogicLocal());
    }

    private BLFacade createRemoteFacade() {
        try {
            ConfigXML c = ConfigXML.getInstance();
            String serviceName = "http://" + c.getBusinessLogicNode() + ":" +
                                c.getBusinessLogicPort() + "/ws/" +
                                c.getBusinessLogicName() + "?wsdl";

            URL url = new URL(serviceName);
            QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
            Service service = Service.create(url, qname);

            return service.getPort(BLFacade.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}



