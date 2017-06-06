package com.example;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
	
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectorConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Root resource (exposed at "update" path)
 */
@Path("/update")
public class Update {

	// /**
	// * Method handling HTTP GET requests. The returned object will be sent
	// * to the client as "text/plain" media type.
	// *
	// * @return String that will be returned as a text/plain response.
	// */
	// @GET
	// @Produces(MediaType.TEXT_PLAIN)
	// public String getIt() {
	// return "Hello, Heroku!";
	// }
	
	static final String USERNAME = "charmer@soliantconsulting.com.vip.qa";
	static final String PASSWORD = "cjhvip2017LcCwLb5ovFvHIPmbLbpAEOHv";
	static final String AUTHENDPOINT = "https://test.salesforce.com/services/Soap/c/40.0/";

	static EnterpriseConnection connection;


	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String parseXML(@QueryParam("xmlRecords") String xmlRecords) {
		xmlRecords=StaticXmlString.getStringXMl();

		Map<String, String> xmlDataMap = new HashMap<String, String>();
		ConnectorConfig config = new ConnectorConfig();
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setAuthEndpoint(AUTHENDPOINT);

		try {

			connection = Connector.newConnection(config);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));
			Document doc = db.parse(is);

			NodeList nodes1 = doc.getElementsByTagName("TRACKINGID");
			Element line2 = (Element) nodes1.item(0);
			System.out.println("Tracking Id Is : " + getCharacterDataFromElement(line2));
			xmlDataMap.put("TRACKINGID", getCharacterDataFromElement(line2));

			NodeList statusNode = doc.getElementsByTagName("STATUSID");
			Element statusElement = (Element) statusNode.item(0);
			xmlDataMap.put("STATUSID", getCharacterDataFromElement(statusElement));

			NodeList statusTSNode = doc.getElementsByTagName("STATUSTIMESTAMP");
			Element statusTSElement = (Element) statusTSNode.item(0);
			xmlDataMap.put("STATUSTIMESTAMP", getCharacterDataFromElement(statusTSElement));

			System.out.println("xmlDataMap is " + xmlDataMap);
			
			// display some current settings
	        System.out.println("Auth EndPoint: " + config.getAuthEndpoint());
	        System.out.println("Service EndPoint: "
	                + config.getServiceEndpoint());
	        System.out.println("Username: " + config.getUsername());
	        System.out.println("SessionId: " + config.getSessionId());
	     
	        System.out.println("**********************************");
	        

	        com.sforce.soap.SpearAppraisalAPI.SoapConnection soap =    com.sforce.soap.SpearAppraisalAPI.Connector.newConnection("","");
	        soap.setSessionHeader(config.getSessionId());
	        
	        XStream xStream = new XStream(new DomDriver());
	        xStream.alias("map", java.util.Map.class);
	        String xmlDataString = xStream.toXML(xmlDataMap);
	        
	        com.sforce.soap.SpearAppraisalAPI.OrderResponse response = soap.setMercuryNetworkStatus(xmlDataString);
	                     
	        return response.getErrorDescription();
	        
		} // end of try
		catch (Exception e) {
			//e.printStackTrace();
			//return e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			return sw.toString();
		}

		
		//return xmlDataMap.toString();
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
}
