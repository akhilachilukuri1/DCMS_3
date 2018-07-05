
package DcmsSEI.server;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "DcmsService", targetNamespace = "http://Server/", wsdlLocation = "file:/C:/Users/akhila/Documents/COMP-6231/DCMS_webservices/DcmsService.wsdl")
public class DcmsService
    extends Service
{

    private final static URL DCMSSERVICE_WSDL_LOCATION;
    private final static WebServiceException DCMSSERVICE_EXCEPTION;
    private final static QName DCMSSERVICE_QNAME = new QName("http://Server/", "DcmsService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/Users/akhila/Documents/COMP-6231/DCMS_webservices/DcmsService.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        DCMSSERVICE_WSDL_LOCATION = url;
        DCMSSERVICE_EXCEPTION = e;
    }

    public DcmsService() {
        super(__getWsdlLocation(), DCMSSERVICE_QNAME);
    }

    public DcmsService(WebServiceFeature... features) {
        super(__getWsdlLocation(), DCMSSERVICE_QNAME, features);
    }

    public DcmsService(URL wsdlLocation) {
        super(wsdlLocation, DCMSSERVICE_QNAME);
    }

    public DcmsService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, DCMSSERVICE_QNAME, features);
    }

    public DcmsService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DcmsService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns Dcms
     */
    @WebEndpoint(name = "DcmsPort")
    public Dcms getDcmsPort() {
        return super.getPort(new QName("http://Server/", "DcmsPort"), Dcms.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Dcms
     */
    @WebEndpoint(name = "DcmsPort")
    public Dcms getDcmsPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://Server/", "DcmsPort"), Dcms.class, features);
    }

    private static URL __getWsdlLocation() {
        if (DCMSSERVICE_EXCEPTION!= null) {
            throw DCMSSERVICE_EXCEPTION;
        }
        return DCMSSERVICE_WSDL_LOCATION;
    }

}
