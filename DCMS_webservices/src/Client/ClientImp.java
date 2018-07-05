package Client;

import java.io.File;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import Conf.LogManager;
import Conf.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import DcmsSEI.server.*;
/*Implementation of Client class*/

public class ClientImp {
	LogManager logManager = null;
	Dcms serverLoc = null;
	URL url = null;
	QName qname  = null;
	Service service = null;
	

	/**
	 * 
	 * creates the client instance with
	 * 
	 * @param args
	 *            gets the port number and IP address and creates the ORB object
	 *            with it.
	 * @param location
	 *            gets the location of the client,based on the location the
	 *            appropriate server instance is called to perform the operation
	 *            requested by the manager.
	 * @param ManagerID
	 *            creates the log file with the managerID.
	 */
	ClientImp(String[] args, ServerCenterLocation location, String ManagerID) {
		try {
			QName mtl = new QName("http://Server/","DcmsService");
			if (location == ServerCenterLocation.MTL) {
				url = new URL("http://localhost:3333/MTL?wsdl");
				service = Service.create(url,mtl);
				serverLoc = service.getPort(Dcms.class);
			} else if (location == ServerCenterLocation.LVL) {
				url = new URL("http://localhost:3333/LVL?wsdl");
				service = Service.create(url,mtl);
				serverLoc = service.getPort(Dcms.class);
			} else if (location == ServerCenterLocation.DDO) {
				url = new URL("http://localhost:3333/DDO?wsdl");
				service = Service.create(url,mtl);
				serverLoc = service.getPort(Dcms.class);
			}
			boolean mgrID = new File(Constants.LOG_DIR + ManagerID).mkdir();
			logManager = new LogManager(ManagerID);
		} catch (Exception e) {
			System.out.println("ERROR : " + e);
			e.printStackTrace(System.out);
		}
	}

	/**
	 * If the teacher record is created, It Displays the record ID of the teacher
	 * record created on the server with the values given by the manager. If the
	 * teacher record is not created it displays the message.
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param teacherField
	 *            values of the teacher attribute concatenated by the comma and are
	 *            sent to the server
	 * 
	 */
	public String createTRecord(String managerID, String teacherField) {
		logManager.logger.log(Level.INFO,
				"Initiating T record object creation request");
		String result = "";
		String teacherID = "";
		teacherID = serverLoc.createTRecord(managerID, teacherField);
		if (teacherID != null)
			result = "Teacher record is created and assigned with " + teacherID;
		else
			result = "Teacher record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	/**
	 * If the student record is created, It Displays the record ID of the student
	 * record created on the server with the values given by the manager. If the
	 * teacher record is not created it displays the message.
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param studentFields
	 *            values of the student attribute concatenated by the comma and are
	 *            sent to the server
	 * 
	 */
	public String createSRecord(String managerID, String studentFields) {
		logManager.logger.log(Level.INFO,
				"Initiating S record object creation request");
		String result = "";
		String studentID = "";
		studentID = serverLoc.createSRecord(managerID, studentFields);
		if (studentID != null)
			result = "student record is created and assigned with " + studentID;
		else
			result = "student record is not created";
		logManager.logger.log(Level.INFO, result);
		return result;
	}

	/**
	 *
	 *
	 * invokes record count on the corresponding server to get record count on all
	 * the servers
	 * 
	 */
	public String getRecordCounts() {
		String count = "";
		logManager.logger.log(Level.INFO, "Initiating record count request");
		count = serverLoc.getRecordCount();
		logManager.logger.log(Level.INFO, "received....count as follows");
		logManager.logger.log(Level.INFO, count);
		return count;
	}

	/**
	 * invokes edit record on the server return the appropriate message
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param location
	 *            gets the location to transfer the recordID
	 */
	public String transferRecord(String ManagerID, String recordID,
			String location) {
		String message = "";
		logManager.logger.log(Level.INFO, "Initiating the record transfer request");
		message = serverLoc.transferRecord(ManagerID, recordID, location);
		System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}

	/**
	 * invokes edit record on the server return the appropriate message
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param fieldname
	 *            gets the fieldname to be edited for the given recordID
	 * @param newvalue
	 *            gets the newvalue to be replaced to the given fieldname
	 */
	public String editRecord(String managerID, String recordID, String fieldname,
			String newvalue) {
		String message = "";
		logManager.logger.log(Level.INFO,
				managerID + "has Initiated the record edit request for " + recordID);
		message = serverLoc.editRecord(managerID, recordID, fieldname, newvalue);
		// System.out.println(message);
		logManager.logger.log(Level.INFO, message);
		return message;
	}
}